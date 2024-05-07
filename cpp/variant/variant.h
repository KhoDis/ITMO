#pragma once

#include <array>
#include <optional>

#include "variant_storage.h"

// region variant
template <typename... Types>
struct variant : private variant_storage_destructors<(std::is_trivially_destructible_v<Types> && ...), Types...> {
private:
  using base = variant_storage_destructors<(std::is_trivially_destructible_v<Types> && ...), Types...>;

  // region friends
  template <bool, typename...>
  friend struct variant_storage_destructors;

  template <std::size_t I, typename... Ts>
  friend constexpr variant_alternative_t<I, variant<Ts...>>& get(variant<Ts...>& var);
  template <std::size_t I, typename... Ts>
  friend constexpr variant_alternative_t<I, variant<Ts...>>&& get(variant<Ts...>&& var);
  template <std::size_t I, typename... Ts>
  friend constexpr const variant_alternative_t<I, variant<Ts...>>& get(const variant<Ts...>& var);
  template <std::size_t I, typename... Ts>
  friend constexpr const variant_alternative_t<I, variant<Ts...>>&& get(const variant<Ts...>&& var);
  // endregion friends

public:
  using base::valueless_by_exception;

  // region constructors
  // region default constructor
  template <typename First = detail::type_at_t<0, Types...>>
  constexpr variant() noexcept(std::is_nothrow_default_constructible_v<First>)
      requires std::is_default_constructible_v<First> : base(in_place_index<0>) {}
  // endregion default constructor

  // region copy constructor
  constexpr variant(const variant&) requires detail::are_not_copy_constructible<Types...> = delete;

  constexpr variant(const variant&) requires detail::are_trivially_constructible<Types...> = default;

  constexpr variant(const variant& other) noexcept((std::is_nothrow_copy_constructible_v<Types> && ...))
      requires(!detail::are_not_copy_constructible<Types...> && !detail::are_trivially_constructible<Types...>) {
    base::index = other.index();

    if (other.valueless_by_exception()) {
      return;
    }

    detail::visit_indexed(
        [this, &other]<std::size_t I>(in_place_index_t<I>) { base::value.template construct_at<I>(get<I>(other)); },
        other);
  }
  // endregion copy constructor

  // region move constructor
  constexpr variant(variant&&) requires detail::are_not_move_constructible<Types...> = delete;

  constexpr variant(variant&&) noexcept requires detail::are_trivially_move_constructible<Types...> = default;

  constexpr variant(variant&& other) noexcept((std::is_nothrow_move_constructible_v<Types> && ...))
      requires(!detail::are_not_move_constructible<Types...> && !detail::are_trivially_move_constructible<Types...>) {
    base::index = other.index();

    if (other.valueless_by_exception()) {
      return;
    }

    detail::visit_indexed([this, &other]<std::size_t I>(
                              in_place_index_t<I>) { base::value.template construct_at<I>(std::move(get<I>(other))); },
                          other);
  }
  // endregion move constructor

  // region conversion constructor
  template <typename Arg, std::size_t I = detail::best_match_v<Arg, variant>,
            typename Alt = detail::type_at_t<I, Types...>>
  constexpr variant(Arg&& arg) noexcept(std::is_nothrow_constructible_v<Alt, Arg>)
      requires(std::is_constructible_v<Alt, Arg> && !std::is_same_v<std::decay_t<Arg>, variant>)
      : base(in_place_index<I>, std::forward<Arg>(arg)) {}
  // endregion converting constructor

  // region in_place_type constructor
  template <typename Alt, typename... Args>
  constexpr variant(in_place_type_t<Alt>, Args&&... args)
      requires(std::is_constructible_v<Alt, Args...>&& detail::is_unique<Alt, Types...>)
      : base(in_place_index<detail::index_of<Alt, Types...>>, std::forward<Args>(args)...) {}
  // endregion in_place_type constructor

  // region in_place_index constructor
  template <std::size_t I, typename... Args>
  constexpr variant(in_place_index_t<I>, Args&&... args)
      requires(std::is_constructible_v<detail::type_at_t<I, Types...>, Args...>&& I < (sizeof...(Types)))
      : base(in_place_index<I>, std::forward<Args>(args)...) {}
  // endregion in_place_index constructor
  // endregion constructors

  // region assignment
  // region copy assignment
  constexpr variant& operator=(const variant&) requires detail::are_not_copy_assignable<Types...> = delete;

  constexpr variant&
  operator=(const variant&) noexcept requires detail::are_trivially_copy_assignable<Types...> = default;

  constexpr variant& operator=(const variant& other) noexcept((std::is_nothrow_copy_assignable_v<Types> && ...) &&
                                                              (std::is_nothrow_copy_constructible_v<Types> && ...))
      requires(!detail::are_not_copy_assignable<Types...> && !detail::are_trivially_copy_assignable<Types...>) {
    if (this == &other) {
      return *this;
    }

    if (valueless_by_exception() && other.valueless_by_exception()) {
      return *this;
    }

    if (other.valueless_by_exception()) {
      base::reset();

      return *this;
    }

    detail::visit_indexed(
        [this, &other]<std::size_t I>(in_place_index_t<I>) {
          if (this->index() == other.index()) {
            get<I>(*this) = get<I>(other);
          } else {
            emplace<I>(get<I>(other));
          }
        },
        other);

    return *this;
  }
  // endregion copy assignment

  // region move assignment
  constexpr variant& operator=(variant&& other) requires detail::are_not_move_assignable<Types...> = delete;

  constexpr variant&
  operator=(variant&& other) noexcept requires detail::are_trivially_move_assignable<Types...> = default;

  constexpr variant& operator=(variant&& other) noexcept((std::is_nothrow_move_assignable_v<Types> && ...) &&
                                                         (std::is_nothrow_move_constructible_v<Types> && ...))
      requires(!detail::are_not_move_assignable<Types...> && !detail::are_trivially_move_assignable<Types...>) {
    if (valueless_by_exception() && other.valueless_by_exception()) {
      return *this;
    }

    if (other.valueless_by_exception()) {
      base::reset();
      return *this;
    }

    detail::visit_indexed(
        [this, &other]<std::size_t I>(in_place_index_t<I>) {
          if (this->index() == other.index()) {
            get<I>(*this) = std::move(get<I>(other));
          } else {
            emplace<I>(get<I>(std::move(other)));
          }
        },
        other);

    return *this;
  }
  // endregion move assignment

  // region conversion assignment
  template <typename Arg, std::size_t I = detail::best_match_v<Arg, variant>,
            typename Alt = detail::type_at_t<I, Types...>>
  constexpr variant&
  operator=(Arg&& arg) noexcept(std::is_nothrow_assignable_v<Alt&, Arg>&& std::is_nothrow_constructible_v<Alt, Arg>)
      requires(std::is_constructible_v<Alt, Arg>&& std::is_assignable_v<Alt&, Arg> &&
               !std::is_same_v<std::decay_t<Arg>, variant>) {
    if (index() == I) {
      get<I>(*this) = std::forward<Arg>(arg);
      return *this;
    }

    emplace<I>(Alt(std::forward<Arg>(arg)));
    return *this;
  }
  // endregion conversion assignment
  // endregion assignment

  // region index
  constexpr std::size_t index() const noexcept {
    return base::index;
  }
  // endregion index

  // region emplace
  template <typename Alt, typename... Args, std::size_t I = detail::index_of<Alt, Types...>>
  constexpr Alt& emplace(Args&&... args)
      requires(std::is_constructible_v<Alt, Args...>&& I < (sizeof...(Types)) && detail::is_unique<Alt, Types...>) {
    return emplace<I>(std::forward<Args>(args)...);
  }

  template <std::size_t I, typename... Args, typename Alt = variant_alternative_t<I, variant>>
  constexpr variant_alternative_t<I, variant>&
  emplace(Args&&... args) noexcept(std::is_nothrow_constructible_v<Alt, Args...>)
      requires(std::is_constructible_v<Alt, Args...>&& I < (sizeof...(Types))) {
    base::reset();
    base::value.template construct_at<I>(std::forward<Args>(args)...);
    base::index = I;

    return get<I>(*this);
  }
  // endregion emplace

  // region swap
  constexpr void swap(variant& other) noexcept((std::is_nothrow_move_constructible_v<Types> && ...) &&
                                               (std::is_nothrow_swappable_v<Types> && ...)) {
    if (this->valueless_by_exception() && other.valueless_by_exception()) {
      return;
    }

    if (index() == other.index()) {
      detail::visit_indexed(
          [this, &other]<std::size_t I>(in_place_index_t<I>) {
            using std::swap;
            swap(get<I>(*this), get<I>(other));
          },
          *this);
      return;
    }

    std::swap(*this, other);
  }
  // endregion swap

  // region comparison
  friend constexpr bool operator==(const variant& lhs, const variant& rhs) {
    if (lhs.index() != rhs.index()) {
      return false;
    }

    if (lhs.valueless_by_exception()) {
      return true;
    }

    return detail::visit_indexed(
        [&lhs, &rhs]<std::size_t I>(in_place_index_t<I>) { return get<I>(lhs) == get<I>(rhs); }, lhs);
  }

  friend constexpr bool operator!=(const variant& lhs, const variant& rhs) {
    return !(lhs == rhs);
  }

  friend constexpr bool operator<(const variant& lhs, const variant& rhs) {
    if (rhs.valueless_by_exception()) {
      return false;
    }
    if (lhs.valueless_by_exception()) {
      return true;
    }
    if (lhs.index() < rhs.index()) {
      return true;
    }
    if (lhs.index() > rhs.index()) {
      return false;
    }
    return detail::visit_indexed([&lhs, &rhs]<std::size_t I>(in_place_index_t<I>) { return get<I>(lhs) < get<I>(rhs); },
                                 lhs);
  }

  friend constexpr bool operator>(const variant& lhs, const variant& rhs) {
    return rhs < lhs;
  }

  friend constexpr bool operator<=(const variant& lhs, const variant& rhs) {
    return !(rhs < lhs);
  }

  friend constexpr bool operator>=(const variant& lhs, const variant& rhs) {
    return !(lhs < rhs);
  }
  // endregion comparison
};
// endregion variant
