#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

#define UTF_8 0
#define UTF_8_BOM 1
#define UTF_16_LE 2
#define UTF_16_BE 3
#define UTF_32_LE 4
#define UTF_32_BE 5

#define BIG_ENDIAN 1
#define LITTLE_ENDIAN 0

typedef uint8_t endianness_t;
typedef uint8_t utf_t;

const char *UTF_8_BOM_S = "\xEF\xBB\xBF";
const char *UTF_16_LE_BOM_S = "\xFF\xFE";
const char *UTF_16_BE_BOM_S = "\xFE\xFF";
const char *UTF_32_LE_BOM_S = "\xFF\xFE\x00\x00";
const char *UTF_32_BE_BOM_S = "\x00\x00\xFE\xFF";

char *output_file = "<output_file>";
char *input_file = "<input_file>";

int file_error(const char *act, char *name)
{
    printf("Cannot %s file '%s'.\n", act, name);
    return 1;
}

int try_fwrite(const void *buffer, size_t size, size_t count, FILE *stream, char *name)
{
    if (fwrite(buffer, size, count, stream) < count)
    {
        return file_error("write to", name);
    }
    return 0;
}

int try_fseek(FILE *stream, long offset, int origin, char *name)
{
    if (fseek(stream, offset, origin) != 0)
    {
        return file_error("move along", name);
    }
    return 0;
}

utf_t determine_utf_type(FILE *input)
{
    unsigned char bom[4];
    unsigned char ch;
    size_t i;
    for (i = 0; i < 4 && feof(input) == 0; i++)
    {
        bom[i] = fgetc(input);
    }

    if (i >= 3 && !memcmp(bom, UTF_8_BOM_S, 3))
    {
        return UTF_8_BOM;
    }
    if (i >= 4 && !memcmp(bom, UTF_32_LE_BOM_S, 4))
    {
        return UTF_32_LE;
    }
    if (i >= 4 && !memcmp(bom, UTF_32_BE_BOM_S, 4))
    {
        return UTF_32_BE;
    }
    if (i >= 2 && !memcmp(bom, UTF_16_LE_BOM_S, 2))
    {
        return UTF_16_LE;
    }
    if (i >= 2 && !memcmp(bom, UTF_16_BE_BOM_S, 2))
    {
        return UTF_16_BE;
    }
    return UTF_8;
}

int utf8_to_codepoint(FILE *input, uint32_t *byte_32, size_t *reading_step, endianness_t endianness)
{
    uint32_t b0 = 0;
    uint32_t b1 = 0;
    uint32_t b2 = 0;
    uint32_t b3 = 0;
    uint8_t first_byte = fgetc(input);
    (*reading_step)++;
    if ((first_byte & 0b10000000) == 0b00000000)
    {
        b3 = first_byte & 0b01111111;
    }
    else if ((first_byte & 0b11100000) == 0b11000000)
    {
        uint8_t second_byte = fgetc(input);
        b2 = (first_byte >> 2) & 0b00000011;
        b3 = (first_byte << 6) | (second_byte & 0b00111111);
        (*reading_step) += 1;
    }
    else if ((first_byte & 0b11110000) == 0b11100000)
    {
        uint8_t second_byte = fgetc(input);
        uint8_t third_byte = fgetc(input);
        b2 = (first_byte << 4) | ((second_byte >> 2) & 0b00001111);
        b3 = (second_byte << 6) | (third_byte & 0b00111111);
        (*reading_step) += 2;
    }
    else if ((first_byte & 0b11111000) == 0b11110000)
    {
        uint8_t second_byte = fgetc(input);
        uint8_t third_byte = fgetc(input);
        uint8_t fourth_byte = fgetc(input);
        b1 = ((first_byte << 2) & 0b00011100) | ((second_byte >> 4) & 0b00000011);
        b2 = (second_byte << 4) | ((third_byte >> 2) & 0b0001111);
        b3 = (third_byte << 6) | (fourth_byte & 0b00111111);
        (*reading_step) += 3;
    }
    else
    {
        return -2;
    }
    if (endianness != BIG_ENDIAN)
    {
        (*byte_32) = (((uint32_t)b0) << 24) | (((uint32_t)b1) << 16) | (((uint32_t)b2) << 8) | ((uint32_t)b3);
    }
    else
    {
        (*byte_32) = (((uint32_t)b3) << 24) | (((uint32_t)b2) << 16) | (((uint32_t)b1) << 8) | ((uint32_t)b0);
    }
    return 0;
}

int utf16_to_codepoint(FILE *input, uint32_t *writing_byte, size_t *step, endianness_t input_endianness, endianness_t output_endianness)
{
    uint8_t b0;
    uint8_t b1;
    if (input_endianness == BIG_ENDIAN)
    {
        b0 = fgetc(input);
        b1 = fgetc(input);
    }
    else
    {
        b1 = fgetc(input);
        b0 = fgetc(input);
    }
    uint16_t leading = (((uint16_t)b0) << 8) | b1;
    if (leading < 0xd800 || leading > 0xdfff)
    {
        (*writing_byte) = leading;
        (*step) += 2;
    }
    else if (leading >= 0xDC00)
    {
        return -2;
    }
    else
    {
        uint32_t byte_32 = ((uint32_t)(leading & 0x3FF)) << 10;
        if (input_endianness == BIG_ENDIAN)
        {
            b0 = fgetc(input);
            b1 = fgetc(input);
        }
        else
        {
            b1 = fgetc(input);
            b0 = fgetc(input);
        }

        (*step) += 4;

        uint16_t trailing = (((uint16_t)b0) << 8) | b1;
        if (trailing < 0xDC00 || trailing > 0xDFFF)
        {
            return -2;
        }
        else
        {
            byte_32 = byte_32 | ((uint16_t)(trailing & 0x3FF));
            (*writing_byte) = byte_32 + 0x10000;
        }
    }
    if (output_endianness == BIG_ENDIAN)
    {
        (*writing_byte) = (((*writing_byte) >> 24) & 0xff) | (((*writing_byte) << 8) & 0xff0000) | (((*writing_byte) >> 8) & 0xff00) | (((*writing_byte) << 24) & 0xff000000);
    }
    return 0;
}

uint32_t utf32_to_codepoint(FILE *input, endianness_t endianness)
{
    uint8_t b0;
    uint8_t b1;
    uint8_t b2;
    uint8_t b3;
    if (endianness == BIG_ENDIAN)
    {
        b0 = fgetc(input);
        b1 = fgetc(input);
        b2 = fgetc(input);
        b3 = fgetc(input);
    }
    else
    {
        b3 = fgetc(input);
        b2 = fgetc(input);
        b1 = fgetc(input);
        b0 = fgetc(input);
    }
    return (((uint32_t)b0) << 24) | (((uint32_t)b1) << 16) | (((uint32_t)b2) << 8) | ((uint32_t)b3);
}

int utf8_to_utf32(FILE *input, FILE *output, size_t input_size, endianness_t endianness)
{
    for (size_t i = 0; i < input_size;)
    {
        uint32_t writing_byte = 0;
        if (utf8_to_codepoint(input, &writing_byte, &i, endianness) != 0)
        {
            return -2;
        }
        int error = try_fwrite(&writing_byte, sizeof(uint32_t), 1, output, output_file);
        if (error != 0)
        {
            return error;
        }
    }
    return 0;
}

int utf32_to_utf8(FILE *input, FILE *output, size_t input_size, endianness_t endianness)
{
    for (size_t i = 0; i < input_size; i += 4)
    {
        uint32_t byte_32 = utf32_to_codepoint(input, endianness);
        int error;
        if ((byte_32 & 0xFFFFFF80) == 0)
        {
            uint8_t first_byte = byte_32;
            error = try_fwrite(&first_byte, sizeof(uint8_t), 1, output, output_file);
        }
        else if ((byte_32 & 0xFFFFF800) == 0)
        {
            uint8_t first_byte = 0b11000000 | (uint8_t)(byte_32 >> 6);
            error = try_fwrite(&first_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t second_byte = 0b10000000 | (uint8_t)(byte_32 & 0x3F);
            error |= try_fwrite(&second_byte, sizeof(uint8_t), 1, output, output_file);
        }
        else if ((byte_32 & 0xFFFF0000) == 0)
        {
            uint8_t first_byte = 0b11100000 | (uint8_t)(byte_32 >> 12);
            error = try_fwrite(&first_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t second_byte = 0b10000000 | (uint8_t)((byte_32 >> 6) & 0x3F);
            error |= try_fwrite(&second_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t third_byte = 0b10000000 | (uint8_t)(byte_32 & 0x3F);
            error |= try_fwrite(&third_byte, sizeof(uint8_t), 1, output, output_file);
        }
        else if ((byte_32 & 0xFFE00000) == 0)
        {
            uint8_t first_byte = 0b11110000 | (uint8_t)(byte_32 >> 18);
            error = try_fwrite(&first_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t second_byte = 0b10000000 | (uint8_t)((byte_32 >> 12) & 0x3F);
            error |= try_fwrite(&second_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t third_byte = 0b10000000 | (uint8_t)((byte_32 >> 6) & 0x3F);
            error |= try_fwrite(&third_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t fourth_byte = 0b10000000 | (uint8_t)(byte_32 & 0x3F);
            error |= try_fwrite(&fourth_byte, sizeof(uint8_t), 1, output, output_file);
        }
        else
        {
            return -1;
        }
        if (error != 0)
        {
            return error;
        }
    }
    return 0;
}

int utf32_to_utf16(FILE *input, FILE *output, size_t input_size, endianness_t input_endianness, endianness_t output_endianness)
{
    for (size_t i = 0; i < input_size; i++)
    {
        uint32_t byte_32 = utf32_to_codepoint(input, input_endianness);

        int error;
        if (byte_32 < 0x10000)
        {
            uint16_t result = byte_32;
            result = output_endianness == LITTLE_ENDIAN ? ((result >> 8) | (result << 8)) : result;
            error = try_fwrite(&result, sizeof(uint16_t), 1, output, output_file);
        }
        else
        {
            uint32_t l = ((((byte_32 - 0x10000) << 22) >> 22) + 0xDC00) & 0x0000FFFF;
            uint32_t h = ((((byte_32 - 0x10000) << 12) >> 22) + 0xD800) << 16;
            uint32_t result = output_endianness == LITTLE_ENDIAN ? ((h << 16) | (l & 0x0000FFFF)) : ((l << 16) | (h & 0x0000FFFF));
            error = try_fwrite(&result, sizeof(uint32_t), 1, output, output_file);
        }
        if (error != 0)
        {
            return error;
        }
    }
    return 0;
}

int utf16_to_utf32(FILE *input, FILE *output, size_t input_size, endianness_t input_endianness, endianness_t output_endianness)
{
    for (size_t i = 0; i < input_size;)
    {
        uint32_t writing_byte;
        if (utf16_to_codepoint(input, &writing_byte, &i, input_endianness, output_endianness) != 0)
        {
            return -2;
        };

        int error = try_fwrite(&writing_byte, sizeof(uint32_t), 1, output, output_file);
        if (error != 0)
        {
            return error;
        }
    }
    return 0;
}

int utf8_to_utf16(FILE *input, FILE *output, size_t input_size, endianness_t endianness)
{
    for (size_t i = 0; i < input_size;)
    {
        uint32_t byte_32;

        if (utf8_to_codepoint(input, &byte_32, &i, LITTLE_ENDIAN) != 0)
        {
            return -2;
        }
        int error;
        if (byte_32 < 0x10000)
        {
            uint16_t result = byte_32;
            result = endianness == BIG_ENDIAN ? ((result >> 8) | (result << 8)) : result;
            error = try_fwrite(&result, sizeof(uint16_t), 1, output, output_file);
        }
        else
        {
            uint32_t l = ((((byte_32 - 0x10000) << 22) >> 22) + 0xDC00) & 0x0000FFFF;
            uint32_t h = ((((byte_32 - 0x10000) << 12) >> 22) + 0xD800) << 16;
            uint32_t result = endianness == BIG_ENDIAN ? ((h << 16) | (l & 0x0000FFFF)) : ((l << 16) | (h & 0x0000FFFF));
            error = try_fwrite(&result, sizeof(uint32_t), 1, output, output_file);
        }
        if (error != 0)
        {
            return error;
        }
    }
    return 0;
}

int utf16_to_utf8(FILE *input, FILE *output, size_t input_size, endianness_t endianness)
{
    for (size_t i = 0; i < input_size;)
    {
        uint32_t byte_32;
        if (utf16_to_codepoint(input, &byte_32, &i, endianness, LITTLE_ENDIAN) != 0)
        {
            return -2;
        };
        int error;
        if ((byte_32 & 0xFFFFFF80) == 0)
        {
            uint8_t first_byte = byte_32;
            error = try_fwrite(&first_byte, sizeof(uint8_t), 1, output, output_file);
        }
        else if ((byte_32 & 0xFFFFF800) == 0)
        {
            uint8_t first_byte = 0b11000000 | (uint8_t)(byte_32 >> 6);
            error = try_fwrite(&first_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t second_byte = 0b10000000 | (uint8_t)(byte_32 & 0x3F);
            error |= try_fwrite(&second_byte, sizeof(uint8_t), 1, output, output_file);
        }
        else if ((byte_32 & 0xFFFF0000) == 0)
        {
            uint8_t first_byte = 0b11100000 | (uint8_t)(byte_32 >> 12);
            error = try_fwrite(&first_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t second_byte = 0b10000000 | (uint8_t)((byte_32 >> 6) & 0x3F);
            error |= try_fwrite(&second_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t third_byte = 0b10000000 | (uint8_t)(byte_32 & 0x3F);
            error |= try_fwrite(&third_byte, sizeof(uint8_t), 1, output, output_file);
        }
        else if ((byte_32 & 0xFFE00000) == 0)
        {
            uint8_t first_byte = 0b11110000 | (uint8_t)(byte_32 >> 18);
            error = try_fwrite(&first_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t second_byte = 0b10000000 | (uint8_t)((byte_32 >> 12) & 0x3F);
            error |= try_fwrite(&second_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t third_byte = 0b10000000 | (uint8_t)((byte_32 >> 6) & 0x3F);
            error |= try_fwrite(&third_byte, sizeof(uint8_t), 1, output, output_file);
            uint8_t fourth_byte = 0b10000000 | (uint8_t)(byte_32 & 0x3F);
            error |= try_fwrite(&fourth_byte, sizeof(uint8_t), 1, output, output_file);
        }
        else
        {
            return -2;
        }
        if (error != 0)
        {
            return error;
        }
    }
    return 0;
}

int copy_utf8(FILE *input, FILE *output)
{
    int ch = fgetc(input);
    while (ch != EOF)
    {
        fputc(ch, output);
        ch = fgetc(input);
    }
    return 0;
}

int copy_utf16(FILE *input, FILE *output, endianness_t in_e, endianness_t out_e)
{
    int b0 = fgetc(input);
    int b1 = fgetc(input);
    if (in_e != out_e)
    {
        while (b0 != EOF && b1 != EOF)
        {
            fputc(b1, output);
            fputc(b0, output);
            b0 = fgetc(input);
            b1 = fgetc(input);
        }
    }
    else
    {
        fputc(b0, output);
        while (b1 != EOF)
        {
            fputc(b1, output);
            b1 = fgetc(input);
        }
    }
    return 0;
}

int copy_utf32(FILE *input, FILE *output, endianness_t in_e, endianness_t out_e)
{
    int b0 = fgetc(input);
    int b1 = fgetc(input);
    int b2 = fgetc(input);
    int b3 = fgetc(input);
    if (in_e != out_e)
    {
        while (b0 != EOF && b1 != EOF)
        {
            fputc(b3, output);
            fputc(b2, output);
            fputc(b1, output);
            fputc(b0, output);
            b0 = fgetc(input);
            b1 = fgetc(input);
            b2 = fgetc(input);
            b3 = fgetc(input);
        }
    }
    else
    {
        fputc(b0, output);
        fputc(b1, output);
        fputc(b2, output);
        while (b3 != EOF)
        {
            fputc(b3, output);
            b3 = fgetc(input);
        }
    }
    return 0;
}

int convert(FILE *input, FILE *output, utf_t input_encoding, utf_t output_encoding)
{
    int error = 0;
    int result = 0;
    switch (input_encoding)
    {
    case UTF_8:
    {
        error = try_fseek(input, 0, SEEK_END, input_file);
        size_t input_size = ftell(input);
        error |= try_fseek(input, 0, SEEK_SET, input_file);
        if (error != 0)
        {
            return error;
        }
        switch (output_encoding)
        {
        case UTF_8:
            result = copy_utf8(input, output);
            break;
        case UTF_8_BOM:
            error = try_fwrite(UTF_8_BOM_S, sizeof(char), 3, output, output_file);
            result = copy_utf8(input, output);
            break;
        case UTF_16_BE:
            error = try_fwrite(UTF_16_BE_BOM_S, sizeof(char), 2, output, output_file);
            result = utf8_to_utf16(input, output, input_size, BIG_ENDIAN);
            break;
        case UTF_16_LE:
            error = try_fwrite(UTF_32_LE_BOM_S, sizeof(char), 2, output, output_file);
            result = utf8_to_utf16(input, output, input_size, LITTLE_ENDIAN);
            break;
        case UTF_32_BE:
            error = try_fwrite(UTF_32_BE_BOM_S, sizeof(char), 4, output, output_file);
            result = utf8_to_utf32(input, output, input_size, BIG_ENDIAN);
            break;
        case UTF_32_LE:
            error = try_fwrite(UTF_32_LE_BOM_S, sizeof(char), 4, output, output_file);
            result = utf8_to_utf32(input, output, input_size, LITTLE_ENDIAN);
            break;
        default:
            return -1;
        }
        break;
    }
    case UTF_8_BOM:
    {
        error = try_fseek(input, 0, SEEK_END, input_file);
        size_t input_size = ftell(input) - 3;
        error |= try_fseek(input, 3, SEEK_SET, input_file);
        if (error != 0)
        {
            return error;
        }
        switch (output_encoding)
        {
        case UTF_8:
            result = copy_utf8(input, output);
            break;
        case UTF_8_BOM:
            error = try_fwrite(UTF_8_BOM_S, sizeof(char), 3, output, output_file);
            result = copy_utf8(input, output);
            break;
        case UTF_16_BE:
            error = try_fwrite(UTF_16_BE_BOM_S, sizeof(char), 2, output, output_file);
            result = utf8_to_utf16(input, output, input_size, BIG_ENDIAN);
            break;
        case UTF_16_LE:
            error = try_fwrite(UTF_32_LE_BOM_S, sizeof(char), 2, output, output_file);
            result = utf8_to_utf16(input, output, input_size, LITTLE_ENDIAN);
            break;
        case UTF_32_BE:
            error = try_fwrite(UTF_32_BE_BOM_S, sizeof(char), 4, output, output_file);
            result = utf8_to_utf32(input, output, input_size, BIG_ENDIAN);
            break;
        case UTF_32_LE:
            error = try_fwrite(UTF_32_LE_BOM_S, sizeof(char), 4, output, output_file);
            result = utf8_to_utf32(input, output, input_size, LITTLE_ENDIAN);
            break;
        default:
            return -1;
        }
        break;
    }
    case UTF_16_LE:
    {
        error = try_fseek(input, 0, SEEK_END, input_file);
        size_t input_size = ftell(input) - 2;
        error |= try_fseek(input, 2, SEEK_SET, input_file);
        if (error != 0)
        {
            return error;
        }
        switch (output_encoding)
        {
        case UTF_8:
            result = utf16_to_utf8(input, output, input_size, LITTLE_ENDIAN);
            break;
        case UTF_8_BOM:
            error = try_fwrite(UTF_8_BOM_S, sizeof(char), 3, output, output_file);
            result = utf16_to_utf8(input, output, input_size, LITTLE_ENDIAN);
            break;
        case UTF_16_BE:
            error = try_fseek(input, 0, SEEK_SET, input_file);
            result = copy_utf16(input, output, LITTLE_ENDIAN, BIG_ENDIAN);
            break;
        case UTF_16_LE:
            error = try_fseek(input, 0, SEEK_SET, input_file);
            result = copy_utf16(input, output, LITTLE_ENDIAN, LITTLE_ENDIAN);
            break;
        case UTF_32_BE:
            error = try_fwrite(UTF_32_BE_BOM_S, sizeof(char), 4, output, output_file);
            result = utf16_to_utf32(input, output, input_size, LITTLE_ENDIAN, BIG_ENDIAN);
            break;
        case UTF_32_LE:
            error = try_fwrite(UTF_32_LE_BOM_S, sizeof(char), 4, output, output_file);
            result = utf16_to_utf32(input, output, input_size, LITTLE_ENDIAN, LITTLE_ENDIAN);
            break;
        default:
            return -1;
        }
        break;
    }
    case UTF_16_BE:
    {
        error = try_fseek(input, 0, SEEK_END, input_file);
        size_t input_size = ftell(input) - 2;
        error |= try_fseek(input, 2, SEEK_SET, input_file);
        if (error != 0)
        {
            return error;
        }
        switch (output_encoding)
        {
        case UTF_8:
            result = utf16_to_utf8(input, output, input_size, BIG_ENDIAN);
            break;
        case UTF_8_BOM:
            error = try_fwrite(UTF_8_BOM_S, sizeof(char), 3, output, output_file);
            result = utf16_to_utf8(input, output, input_size, BIG_ENDIAN);
            break;
        case UTF_16_BE:
            error = try_fseek(input, 0, SEEK_SET, input_file);
            result = copy_utf16(input, output, BIG_ENDIAN, BIG_ENDIAN);
            break;
        case UTF_16_LE:
            error = try_fseek(input, 0, SEEK_SET, input_file);
            result = copy_utf16(input, output, BIG_ENDIAN, LITTLE_ENDIAN);
            break;
        case UTF_32_BE:
            error = try_fwrite(UTF_32_BE_BOM_S, sizeof(char), 4, output, output_file);
            result = utf16_to_utf32(input, output, input_size, BIG_ENDIAN, BIG_ENDIAN);
            break;
        case UTF_32_LE:
            error = try_fwrite(UTF_32_LE_BOM_S, sizeof(char), 4, output, output_file);
            result = utf16_to_utf32(input, output, input_size, BIG_ENDIAN, LITTLE_ENDIAN);
            break;
        default:
            return -1;
        }
        break;
    }
    case UTF_32_LE:
    {
        error = try_fseek(input, 0, SEEK_END, input_file);
        size_t input_size = ftell(input) - 4;
        error |= try_fseek(input, 4, SEEK_SET, input_file);
        if (error != 0)
        {
            return error;
        }
        switch (output_encoding)
        {
        case UTF_8:
            result = utf32_to_utf8(input, output, input_size, LITTLE_ENDIAN);
            break;
        case UTF_8_BOM:
            error = try_fwrite(UTF_8_BOM_S, sizeof(char), 3, output, output_file);
            result = utf32_to_utf8(input, output, input_size, LITTLE_ENDIAN);
            break;
        case UTF_16_BE:
            error = try_fwrite(UTF_16_BE_BOM_S, sizeof(char), 2, output, output_file);
            result = utf32_to_utf16(input, output, input_size, LITTLE_ENDIAN, BIG_ENDIAN);
            break;
        case UTF_16_LE:
            error = try_fwrite(UTF_16_LE_BOM_S, sizeof(char), 2, output, output_file);
            result = utf32_to_utf16(input, output, input_size, LITTLE_ENDIAN, LITTLE_ENDIAN);
            break;
        case UTF_32_BE:
            error = try_fseek(input, 0, SEEK_SET, input_file);
            result = copy_utf32(input, output, LITTLE_ENDIAN, BIG_ENDIAN);
            break;
        case UTF_32_LE:
            error = try_fseek(input, 0, SEEK_SET, input_file);
            result = copy_utf32(input, output, LITTLE_ENDIAN, LITTLE_ENDIAN);
            break;
        default:
            return -1;
        }
        break;
    }
    case UTF_32_BE:
    {
        error = try_fseek(input, 0, SEEK_END, input_file);
        size_t input_size = ftell(input) - 4;
        error |= try_fseek(input, 4, SEEK_SET, input_file);
        if (error != 0)
        {
            return error;
        }
        switch (output_encoding)
        {
        case UTF_8:
            result = utf32_to_utf8(input, output, input_size, BIG_ENDIAN);
            break;
        case UTF_8_BOM:
            error = try_fwrite(UTF_8_BOM_S, sizeof(char), 3, output, output_file);
            result = utf32_to_utf8(input, output, input_size, BIG_ENDIAN);
            break;
        case UTF_16_BE:
            error = try_fwrite(UTF_16_BE_BOM_S, sizeof(char), 2, output, output_file);
            result = utf32_to_utf16(input, output, input_size, BIG_ENDIAN, BIG_ENDIAN);
            break;
        case UTF_16_LE:
            error = try_fwrite(UTF_16_LE_BOM_S, sizeof(char), 2, output, output_file);
            result = utf32_to_utf16(input, output, input_size, BIG_ENDIAN, LITTLE_ENDIAN);
            break;
        case UTF_32_BE:
            error = try_fseek(input, 0, SEEK_SET != 0, input_file);
            result = copy_utf32(input, output, BIG_ENDIAN, BIG_ENDIAN);
            break;
        case UTF_32_LE:
            error = try_fseek(input, 0, SEEK_SET != 0, input_file);
            result = copy_utf32(input, output, BIG_ENDIAN, LITTLE_ENDIAN);
            break;
        default:
            return -1;
        }
        break;
    }
    default:
        return -1;
    }
    if (error != 0)
    {
        return error;
    }
    return result;
}

int main(int argc, char **argv)
{
    if (argc != 4)
    {
        printf("Wrong arguments.\n");
        printf("Should be <input_file> <output_file> <output_encoding> instead.\n");
        return 1;
    }

    input_file = argv[1];
    output_file = argv[2];

    FILE *input = fopen(input_file, "rb");
    if (input == NULL)
    {
        return file_error("open", input_file);
    }

    unsigned int encoding = determine_utf_type(input);
    rewind(input);

    FILE *output = fopen(output_file, "wb");
    if (output == NULL)
    {
        if (fclose(input))
        {
            return file_error("close", input_file);
        }
        return file_error("open", output_file);
    }

    int result = convert(input, output, encoding, atoi(argv[3]));

    if (result == -1)
    {
        printf("Converting type not found.\n");
        result = 1;
    }
    else if (result == -2)
    {
        printf("Invalid code point.\n");
        result = 1;
    } else if (result != 0) {
        printf("An error happened while working with files.\n");
        result = 2;
    }

    int closing_error = 0;
    if (fclose(input))
    {
        closing_error = file_error("close", input_file);
    }
    if (fclose(output))
    {
        closing_error |= file_error("close", output_file);
    }

    int exit_status = closing_error == 0 ? result : closing_error;
    
    return exit_status;
}