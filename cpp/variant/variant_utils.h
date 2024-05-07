#pragma once

namespace detail {

// region type_at
template <std::size_t I, typename... Ts>
requires(I < sizeof...(Ts)) struct type_at {
  using type = std::tuple_element_t<I, std::tuple<Ts...>>;
};

template <std::size_t I, typename... Ts>
using type_at_t = typename type_at<I, Ts...>::type;
// endregion type_at

// region is_unique
template <typename Target, typename... Options>
struct count : std::integral_constant<std::size_t, 0> {};

template <typename Target, typename... Options>
inline constexpr static std::size_t count_v = count<Target, Options...>::value;

template <typename Target, typename... Options>
struct count<Target, Target, Options...> : std::integral_constant<std::size_t, 1 + count_v<Target, Options...>> {};

template <typename Target, typename Option, typename... Options>
struct count<Target, Option, Options...> : std::integral_constant<std::size_t, count_v<Target, Options...>> {};

template <typename Target, typename... Options>
inline constexpr static bool is_unique = count_v<Target, Options...> == 1;
// endregion is_unique

// region concepts
template <typename... Ts>
concept are_not_copy_constructible = !(std::is_copy_constructible_v<Ts> && ...);
template <typename... Ts>
concept are_trivially_constructible = (std::is_trivially_copy_constructible_v<Ts> && ...);

template <typename... Ts>
concept are_not_move_constructible = !(std::is_move_constructible_v<Ts> && ...);
template <typename... Ts>
concept are_trivially_move_constructible = (std::is_trivially_move_constructible_v<Ts> && ...);

template <typename... Ts>
concept are_not_copy_assignable = !(std::is_copy_constructible_v<Ts> && ...) || !(std::is_copy_assignable_v<Ts> && ...);
template <typename... Ts>
concept are_trivially_copy_assignable =
    (std::is_trivially_copy_constructible_v<Ts> && ...) && (std::is_trivially_copy_assignable_v<Ts> && ...) &&
    (std::is_trivially_destructible_v<Ts> && ...);

template <typename... Ts>
concept are_not_move_assignable = !(std::is_move_constructible_v<Ts> && ...) || !(std::is_move_assignable_v<Ts> && ...);
template <typename... Ts>
concept are_trivially_move_assignable =
    (std::is_trivially_move_constructible_v<Ts> && ...) && (std::is_trivially_move_assignable_v<Ts> && ...) &&
    (std::is_trivially_destructible_v<Ts> && ...);
// endregion concepts

// region find_first
template <typename Target, typename... Options>
struct find_first;

template <typename Target, typename... Options>
inline constexpr std::size_t index_of = find_first<Target, Options...>::value;

template <typename Target, typename... Options>
struct find_first<Target, Target, Options...> : std::integral_constant<std::size_t, 0> {};

template <typename Target, typename Option, typename... Options>
struct find_first<Target, Option, Options...> : std::integral_constant<std::size_t, 1 + index_of<Target, Options...>> {
};
// endregion find_first

// region is_typed
template <class, class = void>
struct is_typed : std::false_type {};

template <class T>
struct is_typed<T, std::void_t<typename T::type>> : std::true_type {};
// endregion is_typed

} // namespace detail
