# TODO List

This document tracks upcoming tasks, improvements, and refactoring needed for the project.

## Feature Enhancements

- [ ] **Implement Dynamic Role-Based Authorization**
  - **Description**: Refactor `CustomUserDetailsService` to dynamically assign authorities based on user roles stored in the database (e.g., from `group_member` or `church_member` entities), instead of the current hardcoded 'USER' role.
  - **Relevant File**: `src/main/java/mitl/IntoTheHeaven/application/service/query/CustomUserDetailsService.java`
  - **Detailed Steps**:
    1.  Modify the `Member` domain and its corresponding persistence layer (`MemberPort`, `MemberPersistenceAdapter`) to fetch role information.
    2.  Update the `createUserDetails` method to transform the fetched roles into a list of `GrantedAuthority` objects.
    3.  Consider how to handle users with multiple roles across different groups or churches.
