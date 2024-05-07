#pragma once

#include "searchers.h"

namespace detail {

// region best_match
template <typename U, typename T, std::size_t Ind>
struct overload {
  static std::integral_constant<std::size_t, Ind> fun(T) requires(requires() { new T[1]{std::declval<U>()}; });
};

template <typename U, typename Variant, typename Inds>
struct all_overloads;

template <typename U, typename... Ts, std::size_t... Inds>
struct all_overloads<U, variant<Ts...>, std::index_sequence<Inds...>> : overload<U, Ts, Inds>... {
  using overload<U, Ts, Inds>::fun...;
};

template <typename U, typename Variant>
using make_overload =
    decltype(all_overloads<U, Variant, std::make_index_sequence<variant_size_v<Variant>>>::fun(std::declval<U>()));

template <typename U, typename Variant>
struct best_match;

template <typename U, typename Variant>
requires(is_typed<make_overload<U, Variant>>::value) struct best_match<U, Variant> : make_overload<U, Variant> {};

template <typename U, typename Variant>
struct best_match : std::integral_constant<std::size_t, variant_npos> {};

template <typename U, typename Variant>
inline constexpr std::size_t best_match_v = best_match<U, Variant>::value;
// endregion best_match

} // namespace detail
