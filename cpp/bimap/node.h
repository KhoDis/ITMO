#pragma once

namespace impl {
  struct left_tag;
  struct right_tag;

  template<typename>
  struct tree_node_t {
    tree_node_t *left_subtree{nullptr};
    tree_node_t *right_subtree{nullptr};
    tree_node_t *parent{nullptr};

    tree_node_t *next() const {
      if (right_subtree) {
        auto *node = right_subtree;
        while (node->left_subtree) {
          node = node->left_subtree;
        }
        return node;
      } else {
        auto *node = this;
        while (node->parent && node->parent->right_subtree == node) {
          node = node->parent;
        }
        return node->parent;
      }
    }

    tree_node_t *prev() const {
      if (left_subtree) {
        auto *node = left_subtree;
        while (node->right_subtree) {
          node = node->right_subtree;
        }
        return node;
      } else {
        auto *node = this;
        while (node->parent && node->parent->left_subtree == node) {
          node = node->parent;
        }
        return node->parent;
      }
    }

    tree_node_t *min() {
      auto *node = this;
      while (node->left_subtree) {
        node = node->left_subtree;
      }
      return node;
    }

    tree_node_t *max() {
      auto *node = this;
      while (node->right_subtree) {
        node = node->right_subtree;
      }
      return node;
    }
  };

  struct fake_node_t : tree_node_t<left_tag>, tree_node_t<right_tag> {};

  template<typename Left, typename Right>
  struct real_node_t : fake_node_t {
    Left left_value;
    Right right_value;

    template<typename LeftArg, typename RightArg>
    real_node_t(LeftArg &&left_value, RightArg &&right_value) : left_value(std::forward<LeftArg>(left_value)),
                                                                right_value(std::forward<RightArg>(right_value)) {};

    template<typename Tag>
    constexpr auto &get_value() {
      if constexpr (std::is_same_v<Tag, left_tag>) {
        return left_value;
      } else {
        return right_value;
      }
    }
  };
} // namespace impl
