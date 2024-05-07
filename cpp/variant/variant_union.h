#pragma once

#include "visitor.h"

// region variant_union
template <bool are_trivially_destructible, typename... Ts>
union variant_union;

template <typename T, typename Next>
union variant_union<false, T, Next> {
  T head;
  Next tail;

  constexpr variant_union() : tail() {}

  template <std::size_t I, typename... Args>
  constexpr variant_union(in_place_index_t<I>, Args&&... args) requires(I == 0) : head(std::forward<Args>(args)...) {}

  template <std::size_t I, typename... Args>
  constexpr variant_union(in_place_index_t<I>, Args&&... args) requires(I > 0)
      : tail(in_place_index<I - 1>, std::forward<Args>(args)...) {}

  ~variant_union() {}
};

template <typename T, typename Next>
union variant_union<true, T, Next> {
  T head;
  Next tail;

  constexpr variant_union() : tail() {}

  template <std::size_t I, typename... Args>
  constexpr variant_union(in_place_index_t<I>, Args&&... args) requires(I == 0) : head(std::forward<Args>(args)...) {}

  template <std::size_t I, typename... Args>
  constexpr variant_union(in_place_index_t<I>, Args&&... args) requires(I > 0)
      : tail(in_place_index<I - 1>, std::forward<Args>(args)...) {}

  ~variant_union() = default;
};
// endregion variant_union

// region variant_union_holder
template <bool are_trivially_destructible, typename... Ts>
struct variant_union_holder {};

template <bool are_trivially_destructible, typename T, typename... Ts>
struct variant_union_holder<are_trivially_destructible, T, Ts...> {
  using Head = T;
  using Tail = variant_union_holder<are_trivially_destructible, Ts...>;
  using Union = variant_union<are_trivially_destructible, Head, Tail>;

  Union data;

  constexpr variant_union_holder() : data() {}

  template <std::size_t I, typename... Args>
  constexpr variant_union_holder(in_place_index_t<I>, Args&&... args)
      : data(in_place_index_t<I>{}, std::forward<Args>(args)...) {}

  template <std::size_t I>
  constexpr T& get() requires(I == 0) {
    return data.head;
  }

  template <std::size_t I>
  constexpr auto& get() requires(I > 0) {
    return data.tail.template get<I - 1>();
  }

  template <std::size_t I>
  constexpr const T& get() const requires(I == 0) {
    return data.head;
  }

  template <std::size_t I>
  constexpr const auto& get() const requires(I > 0) {
    return data.tail.template get<I - 1>();
  }

  template <std::size_t I, typename... Args>
  constexpr void construct_at(Args&&... args) requires(I == 0) {
    new (std::addressof(const_cast<std::remove_cv_t<T>&>(data.head))) T(std::forward<Args>(args)...);
  }

  template <std::size_t I, typename... Args>
  constexpr void construct_at(Args&&... args) requires(I > 0) {
    data.tail.template construct_at<I - 1>(std::forward<Args>(args)...);
  }
};
// endregion variant_union_holder
