#pragma once

#include "variant_union.h"

// region variant_storage
template <bool are_trivially_destructible, typename... Ts>
struct variant_storage {
  variant_union_holder<are_trivially_destructible, Ts...> value;
  std::size_t index = 0;

  constexpr variant_storage() noexcept(std::is_nothrow_default_constructible_v<detail::type_at_t<0, Ts...>>) = default;

  template <std::size_t I, typename... Args>
  constexpr variant_storage(in_place_index_t<I>, Args&&... args)
      : value(in_place_index_t<I>{}, std::forward<Args>(args)...), index(I) {}

  constexpr bool valueless_by_exception() const noexcept {
    return index == variant_npos;
  }
};
// endregion variant_storage

// region variant_storage_destructors
template <bool are_trivially_destructible, typename... Ts>
struct variant_storage_destructors;

template <typename... Ts>
struct variant_storage_destructors<true, Ts...> : variant_storage<true, Ts...> {
  using variant_storage<true, Ts...>::variant_storage;
  using variant_storage<true, Ts...>::valueless_by_exception;

  constexpr void reset() {
    this->index = variant_npos;
  }

  constexpr ~variant_storage_destructors() = default;
};

template <typename... Ts>
struct variant_storage_destructors<false, Ts...> : variant_storage<false, Ts...> {
  using variant_storage<false, Ts...>::variant_storage;

  constexpr void reset() {
    if (!this->valueless_by_exception()) {
      visit([]<typename T>(T& value) { value.~T(); }, static_cast<variant<Ts...>&>(*this));
    }
    this->index = variant_npos;
  }

  constexpr ~variant_storage_destructors() {
    reset();
  }
};
// endregion variant_storage_destructors
