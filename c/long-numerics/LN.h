#ifndef H_LN
#define H_LN

#include <string_view>

class LN
{
public:
    typedef uint32_t base_t;

    LN(long long v = 0);
    LN(const LN &);
    LN(LN &&);
    LN(const char *);
    LN(const std::string_view);

    ~LN();

    LN operator+(const LN &) const;
    LN operator-(const LN &) const;
    LN operator*(const LN &) const;
    LN operator/(const LN &) const;
    LN operator%(const LN &) const;

    LN operator~() const;
    LN operator-() const;

    LN operator+=(const LN &);
    LN operator-=(const LN &);
    LN operator*=(const LN &);
    LN operator/=(const LN &);
    LN operator%=(const LN &);

    bool operator<(const LN &) const;
    bool operator<=(const LN &) const;
    bool operator>(const LN &) const;
    bool operator>=(const LN &) const;
    bool operator==(const LN &) const;
    bool operator!=(const LN &) const;

    LN &operator=(const LN &);
    LN &operator=(LN &&);
    LN &operator=(long long);

    operator long long() const;
    operator bool() const;

    char *c_str() const; // ! must be freed

    inline bool is_nan() const
    {
        return m_state == state_t::nan;
    }

    static LN make_nan();

private:
    enum class state_t
    {
        nan = -2,
        negative = -1,
        neutral = 0,
        positive = 1
    };

    state_t m_state;
    static const base_t m_base = 1000000000;
    base_t *m_data;
    std::size_t m_size = 0;
    std::size_t m_capacity = 0;

    inline base_t get(std::size_t at) const
    {
        return at >= m_size ? 0 : m_data[at];
    }

    inline void set(std::size_t at, base_t what)
    {
        m_data[at] = what;
    }

    LN sum(const LN &) const;

    LN abs() const;

    static LN divide(const LN &, const LN &);
    static LN divide(const LN &, long long);

    int compare(const LN &) const;
    int compare_ignore_sign(const LN &) const;

    void resize(std::size_t);
    void push(base_t digit);
    void pop();
    LN &fix();
};

inline LN operator""_ln(const char *literal)
{
    return LN(literal);
}

#endif