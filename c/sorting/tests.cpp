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

struct Person
{
    std::string surname;
    std::string name;
    std::string patronymic;
    std::size_t number;

    Person(std::string surname, std::string name, std::string patronymic, std::size_t number)
        : surname(surname), name(name), patronymic(patronymic), number(number)
    {
    }

    Person()
        : surname(""), name(""), patronymic(""), number(0)
    {
    }

    friend inline std::ostream &operator<<(std::ostream &os, const Person &p)
    {
        os << p.surname << ' ' << p.name << ' ' << p.patronymic << ' ' << p.number;
        return os;
    }

    friend inline std::istream &operator>>(std::istream &is, Person &p)
    {
        is >> p.surname >> p.name >> p.patronymic >> p.number;
        return is;
    }
};

typedef const std::function<std::vector<Person>(void)> test_t;
const std::string folder = "tests";

struct Test
{
    test_t generate;
    std::string name;

    Test(test_t generate, std::string name)
        : generate(generate), name(name)
    {
    }
};

std::string random_string(std::size_t length, std::size_t seed)
{
    const std::string CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    std::mt19937 generator(seed);
    std::uniform_int_distribution<> distribution(0, CHARACTERS.size() - 1);

    std::string random_string;
    random_string.reserve(length);

    for (std::size_t i = 0; i < length; i++)
    {
        random_string.push_back(CHARACTERS[distribution(generator)]);
    }

    return random_string;
}

test_t fixed_part_test(const size_t size, const size_t length, const bool f1 = false, const bool f2 = false, const bool f3 = false, const bool f4 = false)
{
    static int hash = rand();
    return [=]()
    {
        std::vector<Person> people;
        people.reserve(size);
        for (std::size_t i = 0; i < size; i++)
        {
            srand(size * length + (hash += 765332) + i);
            people.push_back(Person(
                random_string(length, !f1 * (1 * size * length + (hash += 54543) + i) + 123),
                random_string(length, !f2 * (2 * size * length + (hash += 474532) + i) + 5432),
                random_string(length, !f3 * (3 * size * length + (hash += 755) + i) + 543534),
                !f4 * (rand() + (hash += 723554)) + 53458433));
        }
        return people;
    };
}

test_t random_length_test(const size_t size, const size_t max_length)
{
    static int hash = rand();
    return [=]()
    {
        std::vector<Person> people;
        people.reserve(size);
        for (std::size_t i = 0; i < size; i++)
        {
            srand(size * max_length + (hash += 415361) + i);
            people.push_back(Person(
                random_string(1 + (rand() % (max_length - 1)), 1 * size * max_length + (hash += 9876) + i),
                random_string(1 + (rand() % (max_length - 1)), 2 * size * max_length + (hash += 643478265) + i),
                random_string(1 + (rand() % (max_length - 1)), 3 * size * max_length + (hash += 162312) + i),
                rand() + (hash += 723645654) + 54842));
        }
        return people;
    };
}

test_t basic_test(const size_t size, const size_t length)
{
    return fixed_part_test(size, length);
}

void run(Test test)
{
    std::vector<Person> people;

    std::ifstream in("tests\\" + test.name);
    do
    {
        Person t;
        in >> t;
        people.push_back(t);
    } while (!in.eof());
    in.close();

    stable_sort(people.begin(), people.end(), [](const Person &a, const Person &b)
                {
                    if (a.surname != b.surname)
                        return a.surname < b.surname;
                    if (a.name != b.name)
                        return a.name < b.name;
                    if (a.patronymic != b.patronymic)
                        return a.patronymic < b.patronymic;
                    return a.number < b.number;
                });

    auto begin = std::chrono::steady_clock::now();
    system(std::string("lab3 " + folder + "\\" + test.name + " " + folder + "\\out.txt").c_str());
    auto end = std::chrono::steady_clock::now();

    std::vector<Person> sorted;
    std::ifstream out("tests\\out.txt");
    for (int i = 0; i < people.size(); i++)
    {
        Person t;
        out >> t;
        sorted.push_back(t);
    }

    if (!out.eof())
    {
        std::cout << "Test failed: " << std::setw(50) << std::left << test.name << std::endl
                  << "\tExpect: end of file" << std::endl
                  << "\tActual: '" << out.rdbuf() << "'" << std::endl;
        return;
    }

    out.close();

    for (int i = 0; i < people.size(); i++)
    {
        if (sorted[i].surname != people[i].surname || sorted[i].name != people[i].name || sorted[i].patronymic != people[i].patronymic || sorted[i].number != people[i].number)
        {
            std::cout << "Test failed: " << std::setw(50) << std::left << test.name << "didn't match on line #" << i << std::endl
                      << "\tExpect: " << people[i] << std::endl
                      << "\tActual: " << sorted[i] << std::endl;
            return;
        }
    }
    std::cout << "Test passed: " << std::setw(50) << std::left << test.name << "in " << std::chrono::duration_cast<std::chrono::milliseconds>(end - begin).count() << " ms" << std::endl;
}

void generate(Test test)
{
    std::vector<Person> people = test.generate();

    std::ofstream in(folder + "\\" + test.name);
    for (size_t i = 0; i < people.size() - 1; i++)
    {
        in << people[i] << std::endl;
    }
    if (people.size() > 0)
    {
        in << people[people.size() - 1];
    }
    in.close();
}

int main()
{
    system("gcc -o lab3 lab3.c");

    std::vector<Test> tests{
        {basic_test(1, 5),      "size-1_length-5"},
        {basic_test(2, 5),      "size-2_length-5"},
        {basic_test(3, 5),      "size-3_length-5"},
        {basic_test(10, 5),     "size-10_length-5"},
        {basic_test(100, 5),    "size-100_length-5"},
        {basic_test(1000, 5),   "size-1000_length-5"},
        {basic_test(10000, 5),  "size-10000_length-5"},
        {basic_test(100000, 5), "size-100000_length-5"},
        {basic_test(1000, 1),   "size-1000_length-1"},
        {basic_test(1000, 2),   "size-1000_length-2"},
        {random_length_test(1000, 20),      "size-1000_length-random"},
        {random_length_test(10000, 20),     "size-10000_length-random"},
        {random_length_test(100000, 15),    "size-100000_length-random"},
        {random_length_test(1000000, 10),   "size-1000000_length-random"},
        {random_length_test(10000000, 5),   "size-10000000_length-random"},
        {fixed_part_test(1000, 5, true),                    "size-1000_length-5_fixed-1"},
        {fixed_part_test(1000, 5, true, true),              "size-1000_length-5_fixed-12"},
        {fixed_part_test(1000, 5, true, true, true),        "size-1000_length-5_fixed-123"},
        {fixed_part_test(1000, 5, true, true, true, true),  "size-1000_length-5_fixed-1234"},
    };
    for (int i = 0; i < 50; i++)
    {
        tests.push_back({random_length_test(1, 20), "small\\size-1_index-" + std::to_string(i)});
    }
    for (int i = 0; i < 50; i++)
    {
        tests.push_back({random_length_test((i % 12) + 1, 20), "small\\size-1-12_index-" + std::to_string(i)});
    }

    // for (Test test : tests)
    // {
    //     generate(test);
    // }

    for (Test test : tests)
    {
        run(test);
    }
}