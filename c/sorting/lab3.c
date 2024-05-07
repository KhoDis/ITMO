#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <inttypes.h>

/*** Types and Constants ***/
typedef uint64_t number_t;
#define NUMBER_FORMAT PRIu64

#define DATA_LENGTH 21
typedef struct
{
    char surname[DATA_LENGTH];
    char name[DATA_LENGTH];
    char patronymic[DATA_LENGTH];
    number_t number;
} Person;

/*** Return on fail ***/
#define SAFE_ALLOC(people, size, target)   \
    smart_alloc(people, (size), (target)); \
    if (people == NULL)                    \
    {                                      \
        return allocation_error("people"); \
    }

#define SAFE_FOPEN(file, name, mode)        \
    if ((file = fopen(name, mode)) == NULL) \
    {                                       \
        return file_error("open", name);    \
    }

#define SAFE_FCLOSE(file, name)           \
    if (fclose(file))                     \
    {                                     \
        return file_error("close", name); \
    }

/*** User interaction ***/
int file_error(const char *act, char *name)
{
    printf("Cannot %s file '%s'.\n", act, name);
    return 1;
}

int allocation_error(const char *name)
{
    printf("Cannot allocate memory for %s.\n", name);
    return 2;
}

/*** Logic ***/
void smart_alloc(Person *people[], size_t size, size_t *target)
{
    if (size == *target)
    {
        // Will cause error if won't be able to reduce capacity 
        people = realloc(*people, size * sizeof(Person));
        return;
    }
    while (*target > size)
    {
        Person *temp = realloc(*people, (*target) * sizeof(Person));
        if (temp != NULL)
        {
            *people = temp;
            return;
        }
        *target = size + (*target - size) / 2;
    }
}

int numcmp(number_t a, number_t b)
{
    return a == b ? 0 : (a > b ? 1 : -1);
}

int compare(Person *a, Person *b)
{
    int result;

    if ((result = strcmp(a->surname, b->surname)))
    {
        return result;
    }
    if ((result = strcmp(a->name, b->name)))
    {
        return result;
    }
    if ((result = strcmp(a->patronymic, b->patronymic)))
    {
        return result;
    }
    if ((result = numcmp(a->number, b->number)))
    {
        return result;
    }

    return 0;
}

void swap(Person *a, Person *b)
{
    Person t = *a;
    *a = *b;
    *b = t;
}

void sort(Person *people, size_t li, size_t ri)
{
    while (li < ri)
    {
        size_t i = li;
        size_t j = ri;
        size_t mi = li + (ri - li) / 2;
        {
            Person middle = people[mi];

            while (1)
            {
                while (compare(&people[i], &middle) < 0)
                {
                    i++;
                }
                while (compare(&people[j], &middle) > 0)
                {
                    j--;
                }
                if (i >= j)
                {
                    break;
                }
                swap(&people[i++], &people[j--]);
            }
        }
        
        // Tail recursion on the smallest side
        if (mi - i < j - mi)
        {
            sort(people, li, j);
            li = j + 1;
        }
        else
        {
            sort(people, j + 1, ri);
            ri = j;
        }
    }
}

/*** IO ***/
int read(FILE *input, Person *people[], size_t *size, char *input_file)
{
    int alloc_error;
    size_t capacity = 1;

    SAFE_ALLOC(people, 0, &capacity);
    for (*size = 0; !feof(input); (*size)++)
    {
        if ((*size) == capacity)
        {
            capacity *= 2;
            SAFE_ALLOC(people, *size, &capacity);
        }
        Person *current = &(*people)[*size];
        if (4 != fscanf(input, "%s %s %s %" NUMBER_FORMAT, &(current->surname), &(current->name), &(current->patronymic), &(current->number)))
        {
            return file_error("read from", input_file);
        }
    }

    // Reduce capacity to match size
    SAFE_ALLOC(people, *size, size);
    return 0;
}

int print_person(Person *person, const char *format, FILE *output)
{
    return 4 == fprintf(output, format, person->surname, person->name, person->patronymic, person->number);
}

int write(FILE *output, Person *people, size_t size, char *output_file)
{
    int error = 0;
    for (int i = 0; i < size - 1; i++)
    {
        if (print_person(&people[i], "%s %s %s %" NUMBER_FORMAT "\n", output))
        {
            return file_error("write to", output_file);
        }
    }
    // Safe, on 0 elemets program would go 'Cannot read from input' earlier
    if (print_person(&people[size - 1], "%s %s %s %" NUMBER_FORMAT, output))
    {
        return file_error("write to", output_file);
    }

    free(people);
    return 0;
}

/*** Main ***/
int main(int argc, char **argv)
{
    if (argc != 3)
    {
        printf("Wrong arguments.\n");
        printf("Should be <input_file> <output_file> instead.\n");
        return 1;
    }

    char *input_file = argv[1];
    char *output_file = argv[2];

    FILE *input;
    SAFE_FOPEN(input, input_file, "r");

    Person *people = NULL;
    size_t amount = 0;
    int reading_result = read(input, &people, &amount, input_file);

    SAFE_FCLOSE(input, input_file);

    if (reading_result != 0)
    {
        // Message will be displayed on error
        return reading_result;
    }

    sort(people, 0, amount - 1);

    FILE *output;
    SAFE_FOPEN(output, output_file, "w");

    int writing_result = write(output, people, amount, output_file);

    SAFE_FCLOSE(output, output_file);

    if (writing_result != 0)
    {
        // Message will be displayed on error
        return writing_result;
    }

    return 0;
}