#include <stdio.h>
#include <stdlib.h>
#include <math.h>

void sort(float *mat, int w, int h)
{
    for (int i = 0; i < h - 1; i++)
    {
        for (int k = i + 1; k < h; k++)
        {
            if (abs(mat[i * w + i]) < abs(mat[k * w + i]))
            {
                for (int j = 0; j < w; j++)
                {
                    float t = mat[i * w + j];
                    mat[i * w + j] = mat[k * w + j];
                    mat[k * w + j] = t;
                }
            }
        }
    }
}

int eliminate(float *mat, int w, int h)
{
    for (int i = 0; i < h; i++)
    {
        if (mat[i * w + i] == 0.0F)
        {
            return i;
        }
        for (int k = i + 1; k < h; k++)
        {
            float c = mat[k * w + i] / mat[i * w + i];
            for (int j = 0; j < w; j++)
            {
                mat[k * w + j] -= c * mat[i * w + j];
                mat[k * w + i] = 0.0F;
            }
        }
    }
    return -1;
}

int resultate(FILE *output, float *mat, int w, int h)
{
    sort(mat, w, h);

    int result = eliminate(mat, w, h);
    /* 
     * result:
     * -1: one solution
     * !-1: linear dependece vector index
     */
    if (result == -1)
    {
        float *ans = malloc(h * sizeof(float));
        if (ans == NULL) {
            return 2;
        }
        for (int i = h - 1; i >= 0; i--)
        {
            ans[i] = mat[i * w + h];
            for (int j = i + 1; j < h; j++)
            {
                if (j != i)
                {
                    ans[i] = ans[i] - mat[i * w + j] * ans[j];
                }
            }
            ans[i] = ans[i] / mat[i * w + i];
        }
        for (int i = 0; i < h; i++)
        {
            fprintf(output, "%g\n", ans[i]);
        }
        free(ans);
    }
    else
    {
        if (mat[result * w + h] == 0.0F)
        {
            fprintf(output, "many solutions");
        }
        else
        {
            fprintf(output, "no solution");
        }
    }
    return 0;
}

void build(FILE *input, float *mat, int w, int h)
{
    for (int i = 0; i < w * h; i++) {
        fscanf(input, "%f", &mat[i]);
    }
}

int file_error(const char *act, char *name) {
    printf("Cannot %s file '%s'.\n", act, name);
    return 1;
}

int allocation_error(const char *name) {
    printf("Cannot allocate memory for %s.\n", name);
    return 2;
}

int main(int argc, char **argv)
{
    if (argc != 3)
    {
        printf("Wrong arguments.\n");
        printf("Should be <input_file> <output_file> instead.\n");
        return 1;
    }

    FILE *input = fopen(argv[1], "r");
    if (input == NULL)
    {
        return file_error("open", argv[1]);
    }

    int h;
    fscanf(input, "%i", &h);
    int w = h + 1;
    
    float *mat = malloc(w * h * sizeof(float));
    if (mat == NULL) {
        if (fclose(input)) {
            return file_error("close", argv[1]);
        }
        return allocation_error("matrix");
    }

    build(input, mat, w, h);

    if (fclose(input)) {
        free(mat);
        return file_error("close", argv[1]);
    }

    FILE *output = fopen(argv[2], "w");
    if (output == NULL)
    {
        free(mat);
        return file_error("open", argv[2]);
    }

    int isError = resultate(output, mat, w, h);

    free(mat);

    if (isError) { 
        if (fclose(output)) {
            return file_error("close", argv[2]);
        }
        return allocation_error("answer");
    }

    if (fclose(output)) {
        return file_error("close", argv[2]);
    }

    return 0;
}