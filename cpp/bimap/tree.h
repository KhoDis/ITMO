#pragma once

#include "node.h"

#include <cstddef>

namespace impl {
  template<typename Value, typename Tag, typename Compare, typename Valuator>
  struct tree_t : Compare {
    using node_t = impl::tree_node_t<Tag>;

    struct iterator {
      node_t *node;

      iterator(node_t *node) : node(node) {}

      Value &operator*() {
        return Valuator::at(node);
      }

      Value *operator->() {
        return &Valuator::at(node);
      }

      iterator &operator++() {
        node = node->next();
        return *this;
      }

      iterator operator++(int) {
        iterator tmp = *this;
        node = node->next();
        return tmp;
      }

      iterator &operator--() {
        node = node->prev();
        return *this;
      }

      iterator operator--(int) {
        iterator tmp = *this;
        node = node->prev();
        return tmp;
      }

      bool operator==(const iterator &other) const {
        return node == other.node;
      }

      bool operator!=(const iterator &other) const {
        return node != other.node;
      }
    };

    tree_t() = default;

    explicit tree_t(Compare&& compare) : Compare(std::move(compare)) {}

    tree_t(tree_t &&other) : fake_node(std::move(other.fake_node)) {}

    tree_t(const tree_t &other) : fake_node(other.fake_node) {}

    node_t *root() const {
      return fake_node->left_subtree;
    }

    void insert(node_t *node) {
      fake_node->left_subtree = insert(fake_node->left_subtree, node);
    }

    node_t *insert(node_t *root, node_t *node) {
      if (!root) {
        root = node;
        root->left_subtree = nullptr;
        root->right_subtree = nullptr;
        root->parent = fake_node;
        return root;
      }
      if (less(Valuator::at(node), Valuator::at(root))) {
        root->left_subtree = insert(root->left_subtree, node);
        root->left_subtree->parent = root;
        return root;
      }
      root->right_subtree = insert(root->right_subtree, node);
      root->right_subtree->parent = root;
      return root;
    }

    iterator begin() const {
      return iterator(fake_node->min());
    }

    iterator end() const {
      return iterator(fake_node);
    }

    iterator erase(iterator it) {
      node_t *node = it.node;
      if (!node->left_subtree) {
        transplant(node, node->right_subtree);
        return iterator(node->next());
      }
      if (!node->right_subtree) {
        transplant(node, node->left_subtree);
        return iterator(node);
      }
      node_t *y = node->right_subtree->min();
      if (y->parent != node) {
        transplant(y, y->right_subtree);
        y->right_subtree = node->right_subtree;
        y->right_subtree->parent = y;
      }
      transplant(node, y);
      y->left_subtree = node->left_subtree;
      y->left_subtree->parent = y;
      return iterator(y);
    }

    iterator find(const Value &value) {
      node_t *node = root();
      while (node) {
        if (less(value, Valuator::at(node))) {
          node = node->left_subtree;
        } else if (greater(value, Valuator::at(node))) {
          node = node->right_subtree;
        } else {
          return iterator(node);
        }
      }
      return end();
    }

    iterator upper_bound(const Value &value) {
      node_t *node = root();
      node_t *result = fake_node;
      while (node) {
        if (less(value, Valuator::at(node))) {
          result = node;
          node = node->left_subtree;
        } else {
          node = node->right_subtree;
        }
      }
      return iterator(result);
    }

    iterator lower_bound(const Value &value) {
      node_t *node = root();
      node_t *result = fake_node;
      while (node) {
        if (greater(value, Valuator::at(node))) {
          node = node->right_subtree;
        } else {
          result = node;
          node = node->left_subtree;
        }
      }
      return iterator(result);
    }

    void swap(tree_t &other) {
      std::swap(fake_node->left_subtree, other.fake_node->left_subtree);
      if (fake_node->left_subtree) {
        fake_node->left_subtree->parent = fake_node;
      }
      if (other.fake_node->left_subtree) {
        other.fake_node->left_subtree->parent = other.fake_node;
      }
    }

    void transplant(node_t *u, node_t *v) {
      if (!u->parent) {
        fake_node->left_subtree = v;
      } else if (u == u->parent->left_subtree) {
        u->parent->left_subtree = v;
      } else {
        u->parent->right_subtree = v;
      }
      if (v) {
        v->parent = u->parent;
      }
    }

    bool less(const Value &a, const Value &b) const {
      return Compare::operator()(a, b);
    }

    bool greater(const Value &a, const Value &b) const {
      return Compare::operator()(b, a);
    }

    bool equal(const Value &a, const Value &b) const {
      return !Compare::operator()(a, b) && !Compare::operator()(b, a);
    }

    node_t *fake_node;
  };
} // namespace impl
