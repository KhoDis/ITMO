#include "LN.h"

#include <iostream>
#include <fstream>
#include <stack>
#include <functional>
#include <string>
#include <cctype>
#include <vector>
#include <stdexcept>
#include <unordered_map>
#include <cctype>
#include <new>

#define ENDNEWLINE true

typedef std::function<LN(std::vector<LN>)> operation_t;

std::stack<LN> numbers;

std::ostream &operator<<(std::ostream &os, const LN &ln)
{
    char *value = numbers.top().c_str();
    os << value;
    free(value);
    return os;
}

class ParseError : public std::runtime_error
{
public:
    ParseError(const std::string &what = "") : std::runtime_error(what) {}
};

struct Operation
{
    const std::size_t arity;
    const operation_t apply;

    Operation(const std::size_t arity, const operation_t apply)
        : arity(arity),
          apply(apply)
    {
    }

    LN operator()(std::vector<LN> args) const
    {
        return apply(args);
    }
};

const std::unordered_map<std::string, Operation> operations = {
    {"+", Operation(2, [](std::vector<LN> args)
                    { return args[0] + args[1]; })},
    {"-", Operation(2, [](std::vector<LN> args)
                    { return args[0] - args[1]; })},
    {"*", Operation(2, [](std::vector<LN> args)
                    { return args[0] * args[1]; })},
    {"/", Operation(2, [](std::vector<LN> args)
                    { return args[0] / args[1]; })},
    {"%", Operation(2, [](std::vector<LN> args)
                    { return args[0] % args[1]; })},
    {"_", Operation(1, [](std::vector<LN> args)
                    { return -args[0]; })},
    {"~", Operation(1, [](std::vector<LN> args)
                    { return ~args[0]; })},
    {">", Operation(2, [](std::vector<LN> args)
                    { return args[0] > args[1] ? 1_ln : 0_ln; })},
    {">=", Operation(2, [](std::vector<LN> args)
                     { return args[0] >= args[1] ? 1_ln : 0_ln; })},
    {"<", Operation(2, [](std::vector<LN> args)
                    { return args[0] < args[1] ? 1_ln : 0_ln; })},
    {"<=", Operation(2, [](std::vector<LN> args)
                     { return args[0] <= args[1] ? 1_ln : 0_ln; })},
    {"==", Operation(2, [](std::vector<LN> args)
                     { return args[0] == args[1] ? 1_ln : 0_ln; })},
    {"!=", Operation(2, [](std::vector<LN> args)
                     { return args[0] != args[1] ? 1_ln : 0_ln; })},
};

void parse(std::ifstream &in)
{
    std::string current;

    while (in >> current)
    {
        if (current.empty())
        {
            throw ParseError("Unable to parse empty line");
        }

        if (current == "NaN")
        {
            numbers.push(LN::make_nan());
        }
        else if (operations.count(current))
        {
            Operation operation = operations.at(current);
            std::size_t arity = operation.arity;
            std::vector<LN> args(arity);
            for (std::size_t j = arity; j > 0; j--)
            {
                if (!numbers.empty())
                {
                    args[j - 1] = numbers.top();
                    numbers.pop();
                }
                else
                {
                    throw ParseError("Not enough arguments for " + std::to_string(arity) + "-ary operation.");
                }
            }
            numbers.push(operation(args));
        }
        else if (std::isdigit(current[0]) || current[0] == '-' || current[0] == '+')
        {
            numbers.push(LN(current));
        }
        else
        {
            throw ParseError("Operation not found: " + current);
        }
    }
}

void write(std::ofstream &out)
{
    while (numbers.size() > 1)
    {
        out << numbers.top() << "\n"; // not endl for consistency
        numbers.pop();
    }
    if (numbers.size() == 1)
    {
        out << numbers.top();
        if (ENDNEWLINE)
        {
            out << "\n";
        }
        numbers.pop();
    }
}

int main(int argc, char **argv)
{
    if (argc != 3)
    {
        std::cout << "Wrong arguments." << std::endl;
        std::cout << "Should be <input_file> <output_file> instead." << std::endl;
        return 1;
    }

    char *input_file = argv[1];
    char *output_file = argv[2];

    std::ifstream input(input_file);
    if (!input.is_open())
    {
        std::cout << "Cannot open file: " << input_file << std::endl;
        return 1;
    }

    try
    {
        parse(input);
    }
    catch (const std::bad_alloc &e)
    {
        std::cout << "Cannot allocate memory. " << e.what() << std::endl;
        return 2;
    }
    catch (const std::out_of_range &e)
    {
        std::cout << "Wrong LN operation. " << e.what() << std::endl;
        return 2;
    }

    input.close();

    std::ofstream output(output_file);

    write(output);

    output.close();
}