#define __CRTDBG_MAP_ALLOC
#include <crtdbg.h>
#define DEBUG_NEW new (_NORMAL_BLOCK, __FILE__, __LINE__)
#define new DEBUG_NEW

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
#include "../LN.h"

typedef std::function<bool(void)> test_t;

struct Test
{
    test_t execute;
    std::string name;
};

#define TEST(test)  \
    {               \
        test, #test \
    }
#define EXPECT_TRUE(expr)                                    \
    if (!(expr))                                             \
    {                                                        \
        std::cout << "\tExpected true: " << (#expr) << "\n"; \
        return false;                                        \
    }
#define EXPECT_EQUAL(expect, actual)                                                     \
    if (!((expect) == (actual)))                                                         \
    {                                                                                    \
        std::cout << "\t" << (#expect) << " == " << (#actual) << std::endl               \
                  << "\tExpect: '" << (expect) << "'\n\tActual: '" << (actual) << "'\n"; \
        return false;                                                                    \
    }
#define EXPECT_FALSE(expr)                                    \
    if (expr)                                                 \
    {                                                         \
        std::cout << "\tExpected false: " << (#expr) << "\n"; \
        return false;                                         \
    }

bool ctor_ll_easy_test()
{
    long long result;

    LN zero;
    result = zero;
    EXPECT_EQUAL(0, result);

    LN zero_ll(0ll);
    result = zero_ll;
    EXPECT_EQUAL(0, result);

    LN one_ll(1ll);
    result = one_ll;
    EXPECT_EQUAL(1, result);

    LN one_m_ll(-1ll);
    result = one_m_ll;
    EXPECT_EQUAL(-1, result);

    LN ll_max(LLONG_MAX);
    result = ll_max;
    EXPECT_EQUAL(LLONG_MAX, result);

    LN ll_min(LLONG_MIN);
    result = ll_min;
    EXPECT_EQUAL(LLONG_MIN, result);

    return true;
}
bool ctor_ll_hard_test()
{
    for (long long i = -123456789; i <= 123456789; i += 1234)
    {
        LN ln(i);
        long long result = ln;
        EXPECT_EQUAL(i, result);
    }

    return true;
}
bool ctor_ll_insane_test()
{
    for (long long i = LLONG_MIN; i < LLONG_MAX - 2234567899999999; i += 1234567899999)
    {
        LN ln(i);
        long long result = ln;
        EXPECT_EQUAL(i, result);
    }

    return true;
}
bool ctor_str_easy_test()
{
    long long result;

    LN zero(std::string_view("0"));
    result = zero;
    EXPECT_EQUAL(0, result);

    LN mzero(std::string_view("-0"));
    result = mzero;
    EXPECT_EQUAL(0, result);

    LN one_ll(std::string_view("1"));
    result = one_ll;
    EXPECT_EQUAL(1, result);

    LN one_m_ll(std::string_view("-1"));
    result = one_m_ll;
    EXPECT_EQUAL(-1, result);

    return true;
}
bool ctor_str_hard_test()
{
    for (long long i = -123456789; i <= 123456789; i += 1234)
    {
        LN ln(std::string_view(std::to_string(i)));
        long long result = ln;
        EXPECT_EQUAL(i, result);
    }

    return true;
}

bool literal_easy_test()
{
    long long result;

    LN zero = 0_ln;
    result = zero;
    EXPECT_EQUAL(0, result);

    LN one = 1_ln;
    result = one;
    EXPECT_EQUAL(1, result);

    LN llmax = 9223372036854775807_ln;
    result = llmax;
    EXPECT_EQUAL(9223372036854775807, result);

    return true;
}
bool literal_hard_test()
{
    for (long long i = 0; i <= 123456789; i += 1234)
    {
        LN ln(std::string_view(std::to_string(i)));
        long long result = operator""_ln(std::to_string(i).c_str());
        EXPECT_EQUAL(i, result);
    }

    return true;
}

bool plus_easy_test()
{
    long long result;
    LN first;
    LN second;

    LN zero(0ll);
    LN one(1ll);
    LN mone(-1ll);

    result = zero + zero;
    EXPECT_EQUAL(0, result);

    result = one + one;
    EXPECT_EQUAL(2, result);

    result = mone + one;
    EXPECT_EQUAL(0, result);

    result = mone + mone;
    EXPECT_EQUAL(-2, result);

    return true;
}
bool plus_hard_test()
{
    // std::cout << (LN(1123) + LN(27)).c_str();
    for (long long i = -123456789; i <= 123456789; i += 1234)
    {
        long long result = LN(i) + LN(6496496 + i);
        EXPECT_EQUAL(i + i + 6496496, result);
    }

    return true;
}
bool plus_insane_test()
{
    for (long long i = -9999999999999999; i <= 9999999999999999; i += 1234567899)
    {
        long long result = LN(i) + LN(5385723459273 + i);
        EXPECT_EQUAL(i + i + 5385723459273, result);
    }

    return true;
}

bool minus_hard_test()
{
    for (long long i = -123456789; i <= 123456789; i += 1234)
    {
        long long result = LN(i) - LN(6496496 + 2 * i);
        EXPECT_EQUAL(-i - 6496496, result);
    }

    return true;
}

bool minus_insane_test()
{
    for (long long i = -9999999999999999; i <= 9999999999999999; i += 1234567899)
    {
        long long result = LN(i) - LN(5385723459273 + 2 * i);
        EXPECT_EQUAL((i) - (5385723459273 + 2 * i), result);
    }

    return true;
}

bool negate_easy_test()
{
    long long result;
    LN first;
    LN second;

    LN zero(0ll);
    LN one(1ll);
    LN mone(-1ll);

    result = -zero;
    EXPECT_EQUAL(0, result);

    result = -one;
    EXPECT_EQUAL(-1, result);

    result = -mone;
    EXPECT_EQUAL(1, result);

    return true;
}
bool negate_hard_test()
{
    for (long long i = -123456789; i <= 123456789; i += 1234)
    {
        long long result = -LN(i);
        EXPECT_EQUAL(-i, result);
    }

    return true;
}
bool multiply_easy_test()
{
    long long result;
    LN first;
    LN second;

    LN zero(0ll);
    LN one(1ll);
    LN mone(-1ll);

    result = zero * zero;
    EXPECT_EQUAL(0, result);

    result = one * one;
    EXPECT_EQUAL(1, result);

    result = mone * one;
    EXPECT_EQUAL(-1, result);

    result = mone * mone;
    EXPECT_EQUAL(1, result);

    return true;
}
bool multiply_hard_test()
{
    for (long long i = -123456789; i <= 123456789; i += 1234)
    {
        long long result = LN(i) * LN(649649 + 2 * i);
        EXPECT_EQUAL(i * (649649 + 2 * i), result);
    }

    return true;
}
bool multiply_insane_test()
{
    for (long long i = -999999999999999; i <= 999999999999999; i += 1234567899)
    {
        long long result = LN(i) * LN(53 + i / 825635812856);
        EXPECT_EQUAL(i * (53 + i / 825635812856), result);
    }

    return true;
}

bool divide_easy_test()
{
    long long result;
    LN first;
    LN second;

    LN zero(0ll);
    LN one(1ll);
    LN mone(-1ll);

    EXPECT_TRUE((zero / zero).is_nan());

    EXPECT_TRUE((one / zero).is_nan());

    EXPECT_EQUAL(0, static_cast<long long>(zero / one));

    EXPECT_FALSE((zero / one).is_nan());

    result = one / one;
    EXPECT_EQUAL(1, result);

    result = mone / one;
    EXPECT_EQUAL(-1, result);

    result = mone / mone;
    EXPECT_EQUAL(1, result);

    return true;
}

bool divide_hard_test()
{
    for (long long i = -123456789; i <= 123456789; i += 12345)
    {
        long long divident = i ^ 42179512359327 + 24309253;
        long long divisor = i * 431 ^ 99812 + 524;
        if (divisor > std::abs(12345678900))
        {
            divisor = 0;
        }
        LN div = LN(divident) / LN(divisor);
        bool nan = div.is_nan();
        if (divisor == 0)
        {
            EXPECT_EQUAL(true, nan);
        }
        else
        {
            EXPECT_EQUAL(false, nan);
            long long result = div;
            EXPECT_EQUAL(divident / divisor, result);
        }
    }

    return true;
}

bool divide_insane_test()
{
    for (long long i = -9999999999999999; i <= 9999999999999999; i += 1234567899)
    {
        long long divident = i ^ 42179512359327 + 24309253;
        long long divisor = i * 431 ^ 99812 + 524;
        if (std::abs(divisor) > 999999999999999)
        {
            divisor = 0;
        }
        LN div = LN(divident) / LN(divisor);
        bool nan = div.is_nan();
        if (divisor == 0)
        {
            EXPECT_EQUAL(true, nan);
        }
        else
        {
            EXPECT_EQUAL(false, nan);
            long long result = div;
            EXPECT_EQUAL(divident / divisor, result);
        }
    }

    return true;
}

bool module_hard_test()
{
    for (long long i = -123456789; i <= 123456789; i += 12345)
    {
        long long divident = i ^ 42179512359327 + 24309253;
        long long divisor = i * 431 ^ 99812 + 524;
        if (divisor > std::abs(12345678900ll))
        {
            divisor = 0;
        }
        LN mod = LN(divident) % LN(divisor);
        bool nan = mod.is_nan();
        if (divisor == 0)
        {
            EXPECT_EQUAL(true, nan);
        }
        else
        {
            EXPECT_EQUAL(false, nan);
            long long result = mod;
            EXPECT_EQUAL(divident % divisor, result);
        }
    }

    return true;
}

bool sqrt_easy_test()
{
    long long result;
    LN first;
    LN second;

    LN zero(0ll);
    LN one(1ll);
    LN mone(-1ll);

    EXPECT_TRUE((~mone).is_nan());

    EXPECT_FALSE((~zero).is_nan());

    result = ~zero;
    EXPECT_EQUAL(0, result);

    EXPECT_FALSE((~one).is_nan());

    result = ~one;
    EXPECT_EQUAL(1, result);

    return true;
}

bool sqrt_hard_test()
{
    for (long long i = -123456789; i <= 123456789; i += 12345)
    {
        long long value = std::hash<long long>{}(i * 592385 ^ (i % 2 == 0 ? -1 : 1));
        LN sqrt = ~LN(value);
        if (value < 0)
        {
            EXPECT_TRUE(sqrt.is_nan());
        }
        else
        {
            EXPECT_FALSE(sqrt.is_nan());
            long long result = sqrt;
            EXPECT_EQUAL(static_cast<long long>(std::sqrt(value)), result);
        }
    }

    return true;
}

void run(Test test)
{
    auto begin = std::chrono::steady_clock::now();
    bool passed = test.execute();
    auto end = std::chrono::steady_clock::now();

    if (passed)
    {
        std::cout << "Test passed: " << test.name << std::left << " in " << std::chrono::duration_cast<std::chrono::milliseconds>(end - begin).count() << " ms" << std::endl;
    }
    else
    {
        std::cout << "Test failed: " << test.name << std::left << std::endl;
    }
}

int main()
{
    std::vector<Test> tests{
        TEST(ctor_ll_easy_test),
        TEST(ctor_str_easy_test),
        TEST(literal_easy_test),
        TEST(ctor_ll_easy_test),
        TEST(ctor_str_easy_test),
        TEST(plus_easy_test),
        TEST(multiply_easy_test),
        TEST(divide_easy_test),
        TEST(sqrt_easy_test),
        TEST(negate_easy_test),

        TEST(minus_hard_test),
        TEST(ctor_ll_hard_test),
        TEST(ctor_str_hard_test),
        TEST(literal_hard_test),
        TEST(ctor_ll_hard_test),
        TEST(ctor_str_hard_test),
        TEST(plus_hard_test),
        TEST(multiply_hard_test),
        TEST(divide_hard_test),
        TEST(module_hard_test),
        TEST(sqrt_hard_test),
        TEST(negate_hard_test),

        TEST(ctor_ll_insane_test),
        TEST(plus_insane_test),
        TEST(minus_insane_test),
        TEST(multiply_insane_test),
        TEST(divide_insane_test),
    };

    for (Test test : tests)
    {
        run(test);
    }

    std::cout << "Testing finished.";

    _CrtDumpMemoryLeaks();
}