#pragma once

#include "tree.h"

#include <cstddef>
#include <stdexcept>

template<typename Left, typename Right, typename CompareLeft = std::less<Left>, typename CompareRight = std::less<Right>>
struct bimap {
  using left_t = Left;
  using right_t = Right;

private:
  using left_tag = impl::left_tag;
  using right_tag = impl::right_tag;

  using left_node_t = impl::tree_node_t<left_tag>;
  using right_node_t = impl::tree_node_t<right_tag>;

  using node_t = impl::fake_node_t;
  using value_node_t = impl::real_node_t<left_t, right_t>;

  template<typename Value, typename Tag>
  struct valuator {
    static Value &at(impl::tree_node_t<Tag> *node) {
      return static_cast<value_node_t *>(static_cast<node_t *>(node))->template get_value<Tag>();
    }
  };

  using left_tree_t = impl::tree_t<left_t, left_tag, CompareLeft, valuator<left_t, left_tag>>;
  using right_tree_t = impl::tree_t<right_t, right_tag, CompareRight, valuator<right_t, right_tag>>;

  using left_tree_iterator = typename left_tree_t::iterator;
  using right_tree_iterator = typename right_tree_t::iterator;

  impl::fake_node_t fake_node;
  left_tree_t left_tree;
  right_tree_t right_tree;
  std::size_t pairs{0};

  template<typename Tag, typename ITag, typename TreeIterator, typename ITreeIterator, typename Value, typename IValue>
  struct base_iterator {
    friend bimap;

  private:
    using inverse_iterator = base_iterator<ITag, Tag, ITreeIterator, TreeIterator, IValue, Value>;
    TreeIterator tree_it;

    impl::tree_node_t<Tag> *get_node() const {
      return tree_it.node;
    }

    base_iterator(TreeIterator tree_it) : tree_it(tree_it) {}

  public:
    Value &operator*() const {
      return static_cast<value_node_t *>(static_cast<node_t *>(tree_it.node))->template get_value<Tag>();
    }

    Value *operator->() {
      return &static_cast<value_node_t *>(static_cast<node_t *>(tree_it.node))->template get_value<Tag>();
    }

    base_iterator &operator++() {
      ++tree_it;
      return *this;
    }

    base_iterator operator++(int) {
      base_iterator tmp = *this;
      ++*this;
      return tmp;
    }

    base_iterator &operator--() {
      --tree_it;
      return *this;
    }

    base_iterator operator--(int) {
      base_iterator tmp = *this;
      --*this;
      return tmp;
    }

    bool operator==(base_iterator const &other) const {
      return tree_it == other.tree_it;
    }

    bool operator!=(base_iterator const &other) const {
      return !(*this == other);
    }

    inverse_iterator flip() {
      return inverse_iterator(ITreeIterator(inverse<ITag>(tree_it.node)));
    }
  };

public:
  using left_iterator = base_iterator<left_tag, right_tag, left_tree_iterator, right_tree_iterator, left_t, right_t>;
  using right_iterator = base_iterator<right_tag, left_tag, right_tree_iterator, left_tree_iterator, right_t, left_t>;

  bimap(CompareLeft compare_left = CompareLeft(),
        CompareRight compare_right = CompareRight()) :
      left_tree(std::move(compare_left)),
      right_tree(std::move(compare_right)) {
    left_tree.fake_node = &fake_node;
    right_tree.fake_node = &fake_node;
  }

  bimap(bimap const &other) : left_tree(other.left_tree), right_tree(other.right_tree) {
    left_tree.fake_node = &fake_node;
    right_tree.fake_node = &fake_node;
    try {
      for (auto it = other.begin_left(); it != other.end_left(); ++it) {
        insert(*it, *it.flip());
      }
    } catch (...) {
      clear();
      throw;
    }
  }

  bimap(bimap &&other) :
      left_tree(std::move(other.left_tree)),
      right_tree(std::move(other.right_tree)) {
    other.swap(*this);
  }

  bimap &operator=(bimap const &other) {
    if (this != &other) {
      bimap(other).swap(*this);
    }
    return *this;
  }

  bimap &operator=(bimap &&other) noexcept {
    if (this != &other) {
      bimap(std::move(other)).swap(*this);
    }
    return *this;
  }

  ~bimap() {
    clear();
  }

  void clear() {
    erase_left(begin_left(), end_left());
    pairs = 0;
  }

  left_iterator insert(left_t const &left, right_t const &right) {
    return insert_common(left, right);
  }

  left_iterator insert(left_t const &left, right_t &&right) {
    return insert_common(left, std::move(right));
  }

  left_iterator insert(left_t &&left, right_t const &right) {
    return insert_common(std::move(left), right);
  }

  left_iterator insert(left_t &&left, right_t &&right) {
    return insert_common(std::move(left), std::move(right));
  }

  left_iterator erase_left(left_iterator it) {
    left_iterator next = it;
    ++next;
    auto node = it.get_node();
    left_tree.erase(node);
    right_tree.erase(inverse<right_tag>(node));
    delete static_cast<value_node_t *>(static_cast<node_t *>(node));
    --pairs;
    return next;
  }

  bool erase_left(left_t const &left) {
    left_iterator it = find_left(left);
    if (it == left_tree.end()) {
      return false;
    }
    erase_left(it);
    return true;
  }

  right_iterator erase_right(right_iterator it) {
    right_iterator next = it;
    ++next;
    auto node = it.get_node();
    right_tree.erase(node);
    left_tree.erase(inverse<left_tag>(node));
    delete static_cast<value_node_t *>(static_cast<node_t *>(node));
    --pairs;
    return next;
  }

  bool erase_right(right_t const &right) {
    right_iterator it = find_right(right);
    if (it == right_tree.end()) {
      return false;
    }
    erase_right(it);
    return true;
  }

  left_iterator erase_left(left_iterator first, left_iterator last) {
    left_iterator next = last;
    while (first != last) {
      first = erase_left(first);
    }
    return next;
  }

  right_iterator erase_right(right_iterator first, right_iterator last) {
    right_iterator next = last;
    while (first != last) {
      first = erase_right(first);
    }
    return next;
  }

  left_iterator find_left(left_t const &left) {
    return left_iterator(left_tree.find(left));
  }

  right_iterator find_right(right_t const &right) {
    return right_iterator(right_tree.find(right));
  }

  right_t const &at_left(left_t const &key) {
    left_iterator it = find_left(key);
    if (it == end_left()) {
      throw std::out_of_range("Key not found");
    }
    return *it.flip();
  }

  left_t const &at_right(right_t const &key) {
    right_iterator it = find_right(key);
    if (it == end_right()) {
      throw std::out_of_range("Key not found");
    }
    return *it.flip();
  }

  template<typename T = right_t, typename = std::enable_if_t<std::is_default_constructible<T>::value>>
  right_t const &at_left_or_default(left_t const &key) {
    left_iterator left_it = find_left(key);
    if (left_it != end_left()) {
      return *left_it.flip();
    }
    right_t right_default = right_t();
    right_iterator right_it = find_right(right_default);
    if (right_it != end_right()) {
      erase_right(right_it);
    }
    return *insert(key, std::move(right_default)).flip();
  }

  template<typename T = left_t, typename = std::enable_if_t<std::is_default_constructible<T>::value>>
  left_t const &at_right_or_default(right_t const &key) {
    right_iterator right_it = find_right(key);
    if (right_it != end_right()) {
      return *right_it.flip();
    }
    left_t left_default = left_t();
    left_iterator left_it = find_left(left_default);
    if (left_it != end_left()) {
      erase_left(left_it);
    }
    return *insert(std::move(left_default), key);
  }

  left_iterator lower_bound_left(const left_t &left) {
    return left_iterator(left_tree.lower_bound(left));
  }

  left_iterator upper_bound_left(const left_t &left) {
    return left_iterator(left_tree.upper_bound(left));
  }

  right_iterator lower_bound_right(const right_t &right) {
    return right_iterator(right_tree.lower_bound(right));
  }

  right_iterator upper_bound_right(const right_t &right) {
    return right_iterator(right_tree.upper_bound(right));
  }

  left_iterator begin_left() const {
    return left_iterator(left_tree.begin());
  }

  left_iterator end_left() const {
    return left_iterator(left_tree.end());
  }

  right_iterator begin_right() const {
    return right_iterator(right_tree.begin());
  }

  right_iterator end_right() const {
    return right_iterator(right_tree.end());
  }

  bool empty() {
    return pairs == 0;
  }

  std::size_t size() const {
    return pairs;
  }

  void swap(bimap &other) {
    left_tree.swap(other.left_tree);
    right_tree.swap(other.right_tree);
    std::swap(pairs, other.pairs);
  }

  friend bool operator==(bimap const &a, bimap const &b) {
    if (a.size() != b.size()) {
      return false;
    }
    for (auto a_it = a.begin_left(), b_it = b.begin_left(); a_it != a.end_left(); ++a_it, ++b_it) {
      if (!a.left_tree.equal(*a_it, *b_it) || !a.right_tree.equal(*a_it.flip(), *b_it.flip())) {
        return false;
      }
    }
    return true;
  }

  friend bool operator!=(bimap const &a, bimap const &b) {
    return !(a == b);
  }

private:
  template<typename ITag, typename Tag>
  static impl::tree_node_t<ITag> *inverse(impl::tree_node_t<Tag> *node) {
    return static_cast<impl::tree_node_t<ITag> *>(static_cast<node_t *>(node));
  }

  template<typename left_t, typename right_t>
  left_iterator insert_common(left_t &&left, right_t &&right) {
    if (left_tree.find(left) != left_tree.end() || right_tree.find(right) != right_tree.end()) {
      return end_left();
    }
    auto *new_node = new value_node_t(std::forward<left_t>(left), std::forward<right_t>(right));
    left_tree.insert(static_cast<left_node_t *>(static_cast<node_t *>(new_node)));
    right_tree.insert(static_cast<right_node_t *>(static_cast<node_t *>(new_node)));
    ++pairs;
    auto *left_node = static_cast<left_node_t *>(static_cast<node_t *>(new_node));
    return left_iterator(left_node);
  }
};
