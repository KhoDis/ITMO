#pragma once

#include "dependant_utils.h"

// region visitor
namespace detail {

template <bool I, typename Visitor, typename... Vs, std::size_t... Is>
constexpr auto make_farray(std::index_sequence<Is...>) noexcept requires(I) {
  return [](Visitor visitor) { return std::forward<Visitor>(visitor)(in_place_index<Is>...); };
}

template <bool I, typename Visitor, typename... Vs, std::size_t... Is>
constexpr auto make_farray(std::index_sequence<Is...>) noexcept requires(!I) {
  return [](Visitor visitor, Vs... variants) {
    return std::forward<Visitor>(visitor)(get<Is>(std::forward<Vs>(variants))...);
  };
}

template <bool I, typename Visitor, typename... Vs, std::size_t... Is, std::size_t... Js, typename... Ls>
constexpr auto make_farray(std::index_sequence<Is...>, std::index_sequence<Js...>, Ls... ls) {
  return std::array<
      std::common_type_t<decltype(make_farray<I, Visitor, Vs...>(std::index_sequence<Is..., Js>{}, ls...))...>,
      sizeof...(Js)>{make_farray<I, Visitor, Vs...>(std::index_sequence<Is..., Js>{}, ls...)...};
}

template <bool I, typename Visitor, typename... Vs>
constexpr auto make_farray() {
  return make_farray<I, Visitor, Vs...>(std::index_sequence<>{},
                                        std::make_index_sequence<variant_size_v<std::decay_t<Vs>>>{}...);
}

constexpr auto& at(auto& array) {
  return array;
}

template <typename Array, typename... Is>
constexpr auto& at(Array& array, std::size_t i, Is... is) {
  return at(array[i], is...);
}

template <bool I, typename Visitor, typename... Vs>
constexpr auto farray = make_farray<I, Visitor, Vs...>();

template <typename Visitor, typename... Vs>
constexpr decltype(auto) visit_indexed(Visitor&& visitor, Vs&&... variants) {
  if ((variants.valueless_by_exception() || ...)) {
    throw bad_variant_access{};
  }

  return at(farray<true, Visitor&&, Vs&&...>, variants.index()...)(std::forward<Visitor>(visitor));
}

} // namespace detail

template <typename Visitor, typename... Vs>
constexpr decltype(auto) visit(Visitor&& visitor, Vs&&... variants) {
  if ((variants.valueless_by_exception() || ...)) {
    throw bad_variant_access{};
  }

  return detail::at(detail::farray<false, Visitor&&, Vs&&...>, variants.index()...)(std::forward<Visitor>(visitor),
                                                                                    std::forward<Vs>(variants)...);
}
// endregion visitor
