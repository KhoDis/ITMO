#include <vector>
#include <string>
#include <fstream>
#include <algorithm>
#include <iostream>
#include <random>
#include <functional>
#include <cstdlib>
#include <chrono>
#include <iomanip>
#include <cmath>
#include <sstream>

#define ISVS 0

struct prim_t
{
    long long num;
    bool is_nan;

    prim_t(long long num = 0) : is_nan(false), num(num)
    {
    }

    friend std::ostream &operator<<(std::ostream &os, const prim_t &pr)
    {
        if (pr.is_nan)
        {
            os << "NaN";
        }
        else
        {
            os << pr.num;
        }
        return os;
    }

    static prim_t nan()
    {
        prim_t a;
        a.is_nan = true;
        return a;
    }
};
typedef std::function<prim_t(std::vector<prim_t>)> invoke_t;

class Expression
{
public:
    virtual ~Expression(){};
    virtual prim_t evaluate() = 0;
    virtual std::string to_string(bool new_line = true) = 0;
};
class Primitive : public Expression
{
public:
    Primitive(prim_t primitive)
        : m_primitive(primitive) {}

    prim_t evaluate()
    {
        return m_primitive;
    }
    std::string to_string(bool new_line = true)
    {
        return (m_primitive.is_nan ? "NaN" : std::to_string(m_primitive.num)) + (new_line ? "\n" : "");
    }

private:
    prim_t m_primitive;
};
class BinaryOperation : public Expression
{
public:
    BinaryOperation(Expression *op1, Expression *op2, std::string sign)
        : m_op1(op1), m_op2(op2), m_sign(sign)
    {
    }

    prim_t evaluate()
    {
        return apply(m_op1->evaluate(), m_op2->evaluate());
    }
    std::string to_string(bool new_line = true)
    {
        return (m_op1->to_string() + m_op2->to_string() + m_sign) + (new_line ? "\n" : "");
    }

protected:
    virtual ~BinaryOperation()
    {
        delete m_op1;
        delete m_op2;
    }
    virtual prim_t apply(prim_t, prim_t) = 0;

private:
    Expression *m_op1, *m_op2;
    std::string m_sign;
};
class UnaryOperation : public Expression
{
public:
    UnaryOperation(Expression *op, std::string sign)
        : m_op(op), m_sign(sign)
    {
    }

    prim_t evaluate()
    {
        return apply(m_op->evaluate());
    }
    std::string to_string(bool new_line = true)
    {
        return m_op->to_string() + m_sign + (new_line ? "\n" : "");
    }

protected:
    virtual ~UnaryOperation()
    {
        delete m_op;
    }
    virtual prim_t apply(prim_t) = 0;

private:
    Expression *m_op;
    std::string m_sign;
};

#define BINARY(name, op, sign)                   \
    class name : public BinaryOperation          \
    {                                            \
    public:                                      \
        name(Expression *op1, Expression *op2)   \
            : BinaryOperation(op1, op2, sign) {} \
                                                 \
    private:                                     \
        prim_t apply(prim_t a, prim_t b)         \
        {                                        \
            return (op);                         \
        }                                        \
    };
#define UNARY(name, op, sign)              \
    class name : public UnaryOperation     \
    {                                      \
    public:                                \
        name(Expression *op1)              \
            : UnaryOperation(op1, sign) {} \
                                           \
    private:                               \
        prim_t apply(prim_t a)             \
        {                                  \
            return (op);                   \
        }                                  \
    };
BINARY(Add, (a.is_nan || b.is_nan) ? prim_t::nan() : (a.num + b.num), "+");
BINARY(Subtract, (a.is_nan || b.is_nan) ? prim_t::nan() : (a.num - b.num), "-");
BINARY(Multiply, (a.is_nan || b.is_nan) ? prim_t::nan() : (a.num *b.num), "*");
BINARY(Divide, (a.is_nan || b.is_nan || b.num == 0) ? prim_t::nan() : (a.num / b.num), "/");
BINARY(Modulo, (a.is_nan || b.is_nan || b.num == 0) ? prim_t::nan() : (a.num % b.num), "%");
UNARY(Negate, a.is_nan ? prim_t::nan() : -a.num, "_");
UNARY(Sqrt, (a.is_nan || a.num < 0) ? prim_t::nan() : sqrt(a.num), "~");
BINARY(Greater, (a.is_nan || b.is_nan) ? 0 : (a.num > b.num ? 1 : 0), ">");
BINARY(GreaterEqual, (a.is_nan || b.is_nan) ? 0 : (a.num >= b.num ? 1 : 0), ">=");
BINARY(Less, (a.is_nan || b.is_nan) ? 0 : (a.num < b.num ? 1 : 0), "<");
BINARY(LessEqual, (a.is_nan || b.is_nan) ? 0 : (a.num <= b.num ? 1 : 0), "<=");
BINARY(Equal, (a.is_nan || b.is_nan) ? 0 : (a.num == b.num ? 1 : 0), "==");
BINARY(NotEqual, (a.is_nan || b.is_nan) ? 1 : (a.num != b.num ? 1 : 0), "!=");
#undef UNARY
#undef BINARY

typedef std::function<std::vector<Expression *>(void)> test_t;

struct Test
{
    test_t generate;
    std::string name;
};

template <typename B>
Expression *generate_unary(std::ptrdiff_t depth, unsigned step1 = 1)
{
    if (depth > 0)
    {
        return new B(generate_unary<B>(depth - step1, step1));
    }
    else
    {
        return new Primitive(rand() - RAND_MAX / 2);
    }
}

template <typename B>
Expression *generate_binary(std::ptrdiff_t depth, unsigned step1 = 1, unsigned step2 = 1)
{
    if (depth > 0)
    {
        return new B(generate_binary<B>(depth - step1, step1, step2), generate_binary<B>(depth - step2, step1, step2));
    }
    else
    {
        return new Primitive(rand() - RAND_MAX / 2);
    }
}

Expression *generate_random(std::ptrdiff_t depth, prim_t range)
{
    if (depth > 0)
    {
        int binary_or_unary = rand() % 4;
        if (binary_or_unary == 0)
        {
            switch (1 + rand() % 3)
            {
            case 1:
                return new Sqrt(generate_random(depth - (1 + rand() % 3), range));
            default:
                return new Negate(generate_random(depth - (1 + rand() % 3), range));
            }
        }
        else
        {
            switch (1 + rand() % 11)
            {
            case 1:
                return new Add(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            case 2:
                return new Subtract(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            case 3:
                return new Multiply(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            case 4:
                return new Divide(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            case 5:
                return new Modulo(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            case 6:
                return new Greater(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            case 7:
                return new GreaterEqual(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            case 8:
                return new Less(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            case 9:
                return new LessEqual(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            case 10:
                return new Equal(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            default:
                return new NotEqual(generate_random(depth - (1 + rand() % 3), range), generate_random(depth - (1 + rand() % 3), range));
            }
        }
    }
    else
    {
        int sign = rand() > (RAND_MAX / 2) ? 1 : -1;
        return new Primitive(sign * (range.num - (rand() % (range.num + 1))));
    }
}

template <typename B>
test_t depth_binary_test(std::ptrdiff_t depth, std::size_t amount, unsigned step1 = 1, unsigned step2 = 1)
{
    return [=]()
    {
        std::vector<Expression *> operations;
        for (std::size_t i = 0; i < amount; i++)
        {
            operations.push_back(generate_binary<B>(depth, step1, step2));
        }
        return operations;
    };
}

template <typename B>
test_t depth_unary_test(std::ptrdiff_t depth, std::size_t amount)
{
    return [=]()
    {
        std::vector<Expression *> operations;
        for (std::size_t i = 0; i < amount; i++)
        {
            operations.push_back(generate_unary<B>(depth));
        }
        return operations;
    };
}

test_t test_test()
{
    return [=]()
    {
        std::vector<Expression *> operations;
        operations.push_back(new Sqrt(new Modulo(new Primitive(1), new Negate(new Primitive(3865)))));
        return operations;
    };
}

test_t depth_random_test(std::ptrdiff_t depth, std::size_t amount, prim_t range = 500)
{
    return [=]()
    {
        std::vector<Expression *> operations;
        for (std::size_t i = 0; i < amount; i++)
        {
            operations.push_back(generate_random(depth, range));
        }
        return operations;
    };
}

std::vector<Expression *> generate(Test test)
{
    std::vector<Expression *> operations = test.generate();

#if ISVS
    std::ofstream in("tests\\inputs\\" + test.name);
#else
    std::ofstream in("inputs\\" + test.name);
#endif

    if (!in.is_open())
    {
        std::cout << "Unable to open input file";
        std::exit(1);
    }
    for (size_t i = 0; i < operations.size() - 1; i++)
    {
        in << operations[i]->to_string(false) << std::endl;
    }
    if (operations.size() > 0)
    {
        in << operations[operations.size() - 1]->to_string(false);
    }
    in.close();

    return operations;
}

void run_parser(Test test)
{
    std::vector<Expression *> operations = generate(test);

    auto begin = std::chrono::steady_clock::now();
#if ISVS
    system(std::string("lab4 tests\\inputs\\" + test.name + " tests\\outputs\\" + test.name).c_str());
#else
    system(std::string("cd .. && lab4 tests\\inputs\\" + test.name + " tests\\outputs\\" + test.name).c_str());
#endif
    auto end = std::chrono::steady_clock::now();

#if ISVS
    std::ifstream actual("tests\\outputs\\" + test.name);
#else
    std::ifstream actual("outputs\\" + test.name);
#endif

    if (!actual.is_open())
    {
        std::cout << "Unable to open output file";
        std::exit(1);
    }
    std::string actual_line, expected_line;
    std::string lineB;
    std::size_t index = 0;
    std::stringstream expected;
    for (auto it = operations.rbegin(); it != operations.rend() - 1; it++)
    {
        expected << (*it)->evaluate() << "\n";
    }
    if (operations.size() > 0)
    {
        expected << operations[0]->evaluate();
    }

    for (Expression *e : operations)
    {
        delete e;
    }

    bool found(false);
    while (!expected.eof())
    {
        actual >> actual_line;
        expected >> expected_line;
        index++;
        if (actual_line != expected_line)
        {
            found = true;
            break;
        }
    }

    if (found)
    {
        std::cout << "Test failed: " << std::setw(50) << std::left << test.name << "didn't match on line #" << index << std::endl
                  << "\tExpect: " << expected_line << std::endl
                  << "\tActual: " << actual_line << std::endl;
        return;
    }

    if (!actual.eof())
    {
        std::cout << "Test failed: " << std::setw(50) << std::left << test.name << std::endl
                  << "\tExpect: end of file" << std::endl
                  << "\tActual: '" << actual.rdbuf() << "'" << std::endl;
        return;
    }

    actual.close();

    std::cout << "Test passed: " << std::setw(50) << std::left << test.name << "in " << std::chrono::duration_cast<std::chrono::milliseconds>(end - begin).count() << " ms" << std::endl;
}

int main()
{
#if ISVS
    system("g++ -std=c++17 LN.cpp lab4.cpp -o lab4");
#else
    system("cd .. && g++ -std=c++17 LN.cpp lab4.cpp -o lab4");
#endif

    srand(52325); // сид генерации

    std::vector<Test> tests{
        {depth_binary_test<Add>(2, 3), "add_simple"},
        {depth_binary_test<Subtract>(2, 3), "subtract_simple"},
        {depth_binary_test<Multiply>(2, 3), "multiply_simple"},
        {depth_binary_test<Divide>(2, 6, 6, 1), "divide_simple"},
        {depth_binary_test<Modulo>(2, 6, 6, 1), "modulo_simple"},
        {depth_binary_test<Greater>(2, 3), "greater_simple"},
        {depth_binary_test<GreaterEqual>(2, 3), "greaterequal_simple"},
        {depth_binary_test<Less>(2, 3), "less_simple"},
        {depth_binary_test<LessEqual>(2, 3), "lessequal_simple"},
        {depth_binary_test<Equal>(2, 3), "equal_simple"},
        {depth_binary_test<NotEqual>(2, 3), "notequal_simple"},
        {depth_unary_test<Negate>(2, 3), "negate_simple"},
        {depth_unary_test<Sqrt>(2, 3), "sqrt_simple"},

        {depth_random_test(4, 2, 200), "random_simple"},
        {depth_random_test(10, 10, 1000), "random_medium"},
        {depth_random_test(30, 20, 20000), "random_hard"},

        {depth_random_test(40, 10, 200), "random_insane-200"},
        {depth_random_test(40, 10, 500), "random_insane-500"},
        {depth_random_test(40, 10, 1000), "random_insane-1000"},
        {depth_random_test(40, 10, 20000), "random_insane-20000"},
        {depth_random_test(40, 10, 1000000), "random_insane-1000000"},
        {depth_random_test(40, 10, 10000000), "random_insane-10000000"},
        {depth_random_test(40, 10, INT_MAX), "random_insane-UINT_MAX"}, // may be overflow
        {depth_random_test(40, 10, 10000000000), "random_insane-10000000000"}, // may be overflow
    };

    for (int i = 1; i <= 500; i++)
    { // удобно для поиска багов, если insane не прошли
        tests.push_back({depth_random_test(5, 1, i * 1000), "random-" + std::to_string(i * 1000)});
    }

    for (Test test : tests)
    {
        run_parser(test);
    }

    std::cout << "Testing finished.";
}