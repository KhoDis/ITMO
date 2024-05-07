#include "LN.h"

#include <cstring>
#include <cstdlib>
#include <stdexcept>
#include <cctype>
#include <new>

LN::LN(const LN &that)
    : m_state(that.m_state),
      m_size(that.m_size),
      m_capacity(that.m_size)
{
    this->m_data = static_cast<base_t *>(std::malloc(that.m_size * sizeof(base_t)));
    if (m_data == nullptr)
    {
        throw std::bad_alloc();
    }
    std::memcpy(this->m_data, that.m_data, that.m_size * sizeof(base_t));

    this->fix();
}
LN::LN(LN &&that)
    : LN(that)
{
    that = 0;
    that.fix(); // free memory
}
LN::LN(long long that)
    : m_data(nullptr),
      m_state(that == 0 ? state_t::neutral : (that > 0 ? state_t::positive : state_t::negative)),
      m_size(0),
      m_capacity(0)
{
    while (that != 0)
    {
        push(std::abs(that % m_base));
        that /= m_base;
    }
}
LN::LN(const char *str)
    : LN(std::string_view(str))
{
}
LN::LN(const std::string_view str)
{
    bool sign = false;
    std::size_t offset = 0;
    while (offset < str.size() && !std::isdigit(str[offset]))
    {
        if (str[offset++] == '-')
        {
            sign = !sign;
        }
    }
    if (offset == str.size())
    {
        throw std::out_of_range("No digits in string constructor");
    }
    m_state = sign ? state_t::negative : state_t::positive;
    for (std::size_t i = str.size(); i > offset; i -= 9)
    {
        int digit = 0;
        for (std::size_t j = i < 9 ? offset : (i - 9 > offset ? i - 9 : offset); i > j; j++) // std::max requires <algorithm>
        {
            digit = digit * 10 + (str[j] - '0');
        }
        push(digit);

        if (i < 9)
        {
            break;
        }
    }
    this->fix();
}

LN::~LN()
{
    free(m_data);
    m_data = nullptr;
    m_size = 0; // preventing leaks
    m_capacity = 0;
    m_state = state_t::neutral;
}

LN LN::sum(const LN &b) const
{
    if (m_state == state_t::nan || b.m_state == state_t::nan)
    {
        return make_nan();
    }
    if (m_state == state_t::positive && b.m_state == state_t::positive || m_state == state_t::negative && b.m_state == state_t::negative)
    {
        if (compare_ignore_sign(b) > 0)
        {
            return b.sum(*this);
        }
        // 0 <= b <= a
        LN ab_sum;
        ab_sum.m_state = m_state;
        bool carry = false;
        for (std::size_t i = 0; i < m_size || carry; i++)
        {
            long long digit_sum = static_cast<long long>(get(i)) + b.get(i) + (carry ? 1ll : 0ll);
            carry = digit_sum >= m_base;
            ab_sum.push(static_cast<base_t>(carry ? digit_sum - m_base : digit_sum));
        }
        if (carry)
        {
            ab_sum.push(carry ? 1 : 0);
        }
        return ab_sum.fix();
    }
    else
    {
        if (compare_ignore_sign(b) > 0) // |b| > |a|
        {
            return b.sum(*this);
        }
        // |a| >= |b|
        LN ab_diff;
        ab_diff.m_state = m_state;
        bool borrow = false;
        for (std::size_t i = 0; i < m_size || borrow; i++)
        {
            long long digit_diff = static_cast<long long>(get(i)) - b.get(i) - (borrow ? 1ll : 0ll);
            borrow = digit_diff < 0;
            ab_diff.push(static_cast<base_t>(borrow ? digit_diff + m_base : digit_diff));
        }
        // no more borrow
        return ab_diff.fix();
    }
}

LN LN::operator+(const LN &that) const
{
    return sum(that);
}
LN LN::operator-(const LN &that) const
{
    return sum(-that);
}
LN LN::operator*(const LN &that) const
{
    if (this->m_state == state_t::nan || that.m_state == state_t::nan)
    {
        return make_nan();
    }
    if (this->m_state == state_t::neutral || that.m_state == state_t::neutral)
    {
        return LN();
    }

    if (that > *this)
    {
        return that.operator*(*this);
    }

    LN product;
    for (std::size_t i = 0; i < this->m_size; i++)
    {
        LN term;
        term.resize(this->m_size + that.m_size);
        std::size_t carry = 0;
        term.m_state = state_t::positive;

        for (std::size_t j = 0; j < that.m_size || carry; j++)
        {
            std::size_t digit_product = (static_cast<long long>(this->get(i)) * that.get(j) + carry);
            term.set(i + j, digit_product % m_base);
            carry = digit_product / m_base;
        }
        term.fix();
        product += term;
    }
    product.m_state = this->m_state == that.m_state ? state_t::positive : state_t::negative;
    return product.fix();
}

LN LN::divide(const LN &divident, long long divisor)
{
    if (divisor == 0)
    {
        return make_nan();
    }
    if ((divident.m_state == state_t::nan) || (divident.m_state == state_t::neutral))
    {
        return divident;
    }
    long long rem = 0;
    LN quotient = divident;
    quotient.m_state = ((divisor > 0) == (divident.m_state == state_t::positive)) ? state_t::positive : state_t::negative;
    for (std::size_t i = divident.m_size; i > 0; i--)
    {
        long long digit = rem * m_base + divident.get(i - 1);
        quotient.set(i - 1, static_cast<base_t>(digit / divisor));
        rem = digit % divisor;
    }
    return quotient.fix();
}
LN LN::divide(const LN &divident, const LN &divisor)
{
    if (divisor.m_state == state_t::neutral || divident.m_state == state_t::nan || divisor.m_state == state_t::nan)
    {
        return make_nan();
    }
    if (divident.m_state == state_t::neutral)
    {
        return LN();
    }
    if (divisor.compare_ignore_sign(divident) < 0)
    {
        return LN();
    }
    LN left = -1;
    static const LN one(1);
    LN right = divident.abs() + one;
    while (right - left > one) // Inv: (a, b)
    {
        LN middle = divide(right + left, 2);
        if ((middle * divisor).compare_ignore_sign(divident) >= 0)
        {
            left = middle;
        }
        else
        {
            right = middle;
        }
    }
    left.m_state = divisor.m_state == divident.m_state ? state_t::positive : state_t::negative;
    return left.fix();
}

LN LN::operator/(const LN &that) const
{
    return divide(*this, that);
}
LN LN::operator%(const LN &that) const
{
    if (that.m_state == state_t::neutral || this->m_state == state_t::nan || that.m_state == state_t::nan)
    {
        return make_nan();
    }
    if (this->m_state == state_t::neutral)
    {
        return LN();
    }
    if (that.compare_ignore_sign(*this) < 0)
    {
        LN modulo(*this);
        modulo.m_state = this->m_state;
        return modulo.fix();
    }

    LN modulo = *this - (divide(*this, that) * that);

    if (modulo.m_state != state_t::neutral)
    {
        modulo.m_state = this->m_state;
    }
    return modulo.fix();
}
LN LN::operator+=(const LN &that)
{
    return *this = *this + that;
}
LN LN::operator-=(const LN &that)
{
    return *this = *this - that;
}
LN LN::operator*=(const LN &that)
{
    return *this = *this * that;
}
LN LN::operator/=(const LN &that)
{
    return *this = *this / that;
}
LN LN::operator%=(const LN &that)
{
    return *this = *this % that;
}

LN LN::operator~() const
{
    if (m_state == state_t::negative)
    {
        return make_nan();
    }
    if (m_state == state_t::neutral || m_state == state_t::nan)
    {
        return *this;
    }
    LN left = -1;
    static const LN one(1);
    LN right = *this + one;
    while (right - left > one) // Inv: (a, b)
    {
        LN middle = divide(right + left, 2);
        if (middle * middle <= *this)
        {
            left = middle;
        }
        else
        {
            right = middle;
        }
    }
    left.m_state = state_t::positive;
    return left;
}
LN LN::abs() const
{
    LN absolute(*this);
    if (this->m_state == state_t::negative)
    {
        absolute.m_state = state_t::positive;
    }
    return absolute;
}
LN LN::operator-() const
{
    LN that = *this;
    if (that.m_state == state_t::positive)
    {
        that.m_state = state_t::negative;
        return that;
    }
    if (that.m_state == state_t::negative)
    {
        that.m_state = state_t::positive;
        return that;
    }
    return that;
}

bool LN::operator<(const LN &that) const
{
    return (this->m_state == state_t::nan || that.m_state == state_t::nan) ? false : compare(that) > 0;
}
bool LN::operator<=(const LN &that) const
{
    return (this->m_state == state_t::nan || that.m_state == state_t::nan) ? false : compare(that) >= 0;
}
bool LN::operator>(const LN &that) const
{
    return (this->m_state == state_t::nan || that.m_state == state_t::nan) ? false : compare(that) < 0;
}
bool LN::operator>=(const LN &that) const
{
    return (this->m_state == state_t::nan || that.m_state == state_t::nan) ? false : compare(that) <= 0;
}
bool LN::operator==(const LN &that) const
{
    return (this->m_state == state_t::nan || that.m_state == state_t::nan) ? false : compare(that) == 0;
}
bool LN::operator!=(const LN &that) const
{
    return (this->m_state == state_t::nan || that.m_state == state_t::nan) ? true : compare(that) != 0;
}

int LN::compare(const LN &that) const
{
    // nan is lower than negative
    if (this->m_state < that.m_state)
    {
        return 1;
    }
    if (this->m_state > that.m_state)
    {
        return -1;
    }

    bool sign = that.m_state == state_t::negative;
    if (this->m_size < that.m_size)
    {
        return sign ? -1 : 1;
    }
    if (this->m_size > that.m_size)
    {
        return sign ? 1 : -1;
    }

    for (std::size_t i = m_size; i > 0; i--)
    {
        if (this->get(i - 1) < that.get(i - 1))
        {
            return sign ? -1 : 1;
        }
        else if (this->get(i - 1) > that.get(i - 1))
        {
            return sign ? 1 : -1;
        }
    }
    return 0;
}
int LN::compare_ignore_sign(const LN &that) const
{
    if (this->m_size < that.m_size)
    {
        return 1;
    }
    if (this->m_size > that.m_size)
    {
        return -1;
    }

    // this.size == that.size
    for (std::size_t i = m_size; i > 0; i--)
    {
        if (this->get(i - 1) < that.get(i - 1))
        {
            return 1;
        }
        else if (this->get(i - 1) > that.get(i - 1))
        {
            return -1;
        }
    }
    return 0;
}

LN &LN::operator=(const LN &that)
{
    if (that.m_data == nullptr || that.m_size == 0)
    {
        *this = 0;
        this->fix(); // free memory
        return *this;
    }

    if (this->m_capacity >= that.m_size)
    {
        std::memcpy(this->m_data, that.m_data, that.m_size * sizeof(base_t));
        this->m_size = that.m_size;
        this->m_state = that.m_state;
    }
    else
    {
        free(this->m_data);
        this->m_data = static_cast<base_t *>(std::malloc(that.m_size * sizeof(base_t)));
        if (m_data == nullptr)
        {
            throw std::bad_alloc();
        }
        this->m_capacity = that.m_size;
        this->operator=(that);
    }

    return this->fix();
}
LN &LN::operator=(LN &&that)
{
    this->operator=(that);

    that = 0;
    that.fix(); // clear memory;
}
LN &LN::operator=(long long that)
{
    m_data = nullptr;
    m_state = that == 0 ? state_t::neutral : (that > 0 ? state_t::positive : state_t::negative);
    m_size = 0;
    m_capacity = 0;

    while (that != 0)
    {
        push(std::abs(that % m_base));
        that /= m_base;
    }
    return *this;
}

LN::operator long long() const
{
    if (m_state == state_t::positive)
    {
        long long converted = 0;
        if ((*this) > LN(LLONG_MAX))
        {
            throw std::out_of_range("LN value is too big for long long");
        }
        for (std::size_t i = m_size; i > 1; i--)
        {
            converted += this->get(i - 1);
            converted *= m_base;
        }
        return converted + this->get(0);
    }
    else if (m_state == state_t::negative)
    {
        long long converted = 0;
        if ((*this) < LN(LLONG_MIN))
        {
            throw std::out_of_range("LN value is too small for long long");
        }
        for (std::size_t i = m_size; i > 1; i--)
        {
            converted -= this->get(i - 1);
            converted *= m_base;
        }
        return converted - this->get(0);
    }
    else if (m_state == state_t::nan)
    {
        throw std::out_of_range("LN::nan cannot be casted to long long");
    }
    else
    {
        // neutral
        return 0;
    }
}
LN::operator bool() const
{
    return m_state != state_t::neutral;
}

char *LN::c_str() const
{
    char *converted;

    if (m_state == state_t::nan)
    {
        converted = static_cast<char *>(std::malloc(4 * sizeof(char)));
        if (converted == nullptr)
        {
            throw std::bad_alloc();
        }
        return std::strcpy(converted, "NaN");
    }

    if (m_state == state_t::neutral)
    {
        converted = static_cast<char *>(std::malloc(2 * sizeof(char)));
        if (converted == nullptr)
        {
            throw std::bad_alloc();
        }
        return std::strcpy(converted, "0");
    }

    int offset = m_state == state_t::negative ? 1 : 0;

    converted = static_cast<char *>(std::malloc(((offset + m_size) * 9 + 1) * sizeof(char)));

    if (converted == nullptr)
    {
        throw std::bad_alloc();
    }

    if (offset)
    {
        converted[0] = '-';
    }
    sprintf(converted + offset, "%d", (m_state == state_t::neutral ? 0 : m_data[m_size - 1]));
    std::size_t used = std::strlen(converted);
    for (std::size_t i = m_size - 1; i > 0; i--)
    {
        sprintf(converted + used, "%09d", get(i - 1));
        used += 9;
    }
    converted[(offset + m_size) * 9] = '\0';
    return converted;
}

LN LN::make_nan() // doesn't allocate
{
    LN nan_value;
    nan_value.m_state = state_t::nan;
    return nan_value;
}

LN &LN::fix()
{
    while (m_size > 1 && this->get(m_size - 1) == 0)
    {
        pop();
    }
    if (m_size == 1 && this->get(0) == 0)
    {
        free(m_data);
        m_data = nullptr;
        m_capacity = 0;
        m_size = 0;
        m_state = state_t::neutral;
    }
    return *this;
}
void LN::resize(std::size_t target_size)
{
    base_t *temp = static_cast<base_t *>(std::realloc(m_data, target_size * sizeof(base_t)));

    if (target_size == 0)
    {
        m_data = nullptr;
        return;
    }

    if (temp != nullptr)
    {
        if (target_size > m_size)
        {
            std::memset(temp + m_size, 0, (target_size - m_size) * sizeof(base_t));
        }
        m_size = target_size;
        m_capacity = target_size;
        m_data = temp;
        return;
    }
    throw std::bad_alloc();
}
void LN::push(base_t digit)
{
    if (m_capacity == 0)
    {
        m_data = static_cast<base_t *>(std::malloc(sizeof(base_t)));
        if (m_data == nullptr)
        {
            throw std::bad_alloc();
        }
        m_capacity = 1;
        return push(digit);
    }
    else if (m_size == m_capacity)
    {
        m_capacity *= 2;
        while (m_capacity > m_size)
        {
            base_t *temp = static_cast<base_t *>(std::realloc(m_data, m_capacity * sizeof(base_t)));
            if (temp != nullptr)
            {
                m_data = temp;
                return push(digit);
            }
            m_capacity = m_size + (m_capacity - m_size) / 2;
        }
        free(m_data);
        throw std::bad_alloc();
    }
    else
    {
        m_data[m_size++] = digit;
    }
}
void LN::pop() // if m_size < 1 then UB
{
    while (m_size * 4 < m_capacity)
    {
        base_t *temp = static_cast<base_t *>(std::realloc(m_data, 2 * m_size * sizeof(base_t)));
        if (temp != nullptr)
        {
            m_capacity = 2 * m_size;
            m_data = temp;
            return pop();
        }
        throw std::bad_alloc();
    }
    m_data[--m_size] = base_t();
    if (m_size == 0)
    {
        free(m_data);
        m_data = nullptr;
        m_capacity = 0;
    }
}