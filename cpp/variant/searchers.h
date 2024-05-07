#pragma once

#include "variant_utils.h"

template <typename... Types>
struct variant;

// region bad_variant_access
struct bad_variant_access : std::exception {
  const char* what() const noexcept override {
    return "bad_variant_access";
  }
};
// endregion bad_variant_access

// region variant_alternative
template <std::size_t I, typename T>
struct variant_alternative;

template <std::size_t I, typename... Types>
struct variant_alternative<I, variant<Types...>> {
  using type = detail::type_at_t<I, Types...>;
};

template <std::size_t I, typename T>
using variant_alternative_t = typename variant_alternative<I, T>::type;

template <std::size_t I, typename T>
struct variant_alternative<I, const T> {
  using type = std::add_const_t<variant_alternative_t<I, T>>;
};
// endregion variant_alternative

// region variant_size
template <typename T>
struct variant_size;

template <typename... Types>
struct variant_size<variant<Types...>> : std::integral_constant<std::size_t, sizeof...(Types)> {};

template <typename... Types>
struct variant_size<const variant<Types...>> : std::integral_constant<std::size_t, sizeof...(Types)> {};

template <typename T>
inline constexpr std::size_t variant_size_v = variant_size<T>::value;
// endregion variant_size

// region variant_npos
inline constexpr std::size_t variant_npos = -1;
// endregion variant_npos

// region in_place
// region in_place_type_t
template <typename T>
struct in_place_type_t {};

template <typename T>
inline constexpr in_place_type_t<T> in_place_type{};
// endregion in_place_type_t

// region in_place_index_t
template <std::size_t I>
struct in_place_index_t {};

template <std::size_t I>
inline constexpr in_place_index_t<I> in_place_index{};
// endregion in_place_index_t
// endregion in_place

// region get
template <std::size_t I, typename... Types>
constexpr variant_alternative_t<I, variant<Types...>>& get(variant<Types...>& var) {
  if (var.index() == I) {
    return var.value.template get<I>();
  }
  throw bad_variant_access();
}
template <std::size_t I, typename... Types>
constexpr variant_alternative_t<I, variant<Types...>>&& get(variant<Types...>&& var) {
  return std::move(get<I>(var));
}
template <std::size_t I, typename... Types>
constexpr const variant_alternative_t<I, variant<Types...>>& get(const variant<Types...>& var) {
  if (var.index() == I) {
    return var.value.template get<I>();
  }
  throw bad_variant_access();
}
template <std::size_t I, typename... Types>
constexpr const variant_alternative_t<I, variant<Types...>>&& get(const variant<Types...>&& var) {
  return std::move(get<I>(var));
}
template <typename U, typename... Types>
constexpr U& get(variant<Types...>& var) {
  return get<detail::index_of<U, Types...>>(var);
}
template <typename U, typename... Types>
constexpr U&& get(variant<Types...>&& var) {
  return get<detail::index_of<U, Types...>>(var);
}
template <typename U, typename... Types>
constexpr const U& get(const variant<Types...>& var) {
  return get<detail::index_of<U, Types...>>(var);
}
template <typename U, typename... Types>
constexpr const U&& get(const variant<Types...>&& var) {
  return get<detail::index_of<U, Types...>>(var);
}
// endregion get

// region get_if
template <std::size_t I, typename... Types>
constexpr std::add_pointer_t<variant_alternative_t<I, variant<Types...>>> get_if(variant<Types...>* var) noexcept {
  return var && var->index() == I ? std::addressof(get<I>(*var)) : nullptr;
}
template <std::size_t I, typename... Types>
constexpr std::add_pointer_t<const variant_alternative_t<I, variant<Types...>>>
get_if(const variant<Types...>* var) noexcept {
  return var && var->index() == I ? std::addressof(get<I>(*var)) : nullptr;
}
template <class T, class... Types>
constexpr std::add_pointer_t<T> get_if(variant<Types...>* var) noexcept {
  return get_if<detail::index_of<T, Types...>>(var);
}
template <class T, class... Types>
constexpr std::add_pointer_t<const T> get_if(const variant<Types...>* var) noexcept {
  return get_if<detail::index_of<T, Types...>>(var);
}
// endregion get_if

// region holds_alternative
template <typename U, typename... Types>
constexpr bool holds_alternative(const variant<Types...>& var) noexcept {
  return var.index() == detail::index_of<U, Types...>;
}
// endregion holds_alternative
