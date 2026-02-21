# Testing & CI/CD Setup

## Tests Overview

The application has **9 unit tests** covering critical functionality:

### AuthServiceTest (4 tests)
- `register_Success` - Verifies user registration
- `register_DuplicateUsername_ThrowsException` - Validates username uniqueness
- `register_DuplicateEmail_ThrowsException` - Validates email uniqueness
- `login_Success` - Verifies login with JWT token generation

### UserServiceTest (5 tests)
- `createUser_Success` - Tests user creation with password encoding
- `createUser_DuplicateUsername_ThrowsException` - Validates duplicate prevention
- `updateUser_Success` - Tests user updates
- `assignStudentToTeacher_Success` - Tests student-teacher assignment
- `deleteUser_Success` - Tests user deletion

## Running Tests

```bash
mvn clean test
```

All tests use **Mockito** for mocking dependencies, ensuring fast execution without database dependencies.

## GitHub Actions CI/CD

A workflow file has been created at `.github/workflows/ci.yml` that automatically runs tests on:
- Push to `main` or `develop` branches
- Pull requests to `main`

The workflow:
1. Checks out code
2. Sets up JDK 17
3. Runs all tests with Maven
4. Builds the application

## Setting Up Branch Protection

To enforce testing before merging:

1. Go to your GitHub repository
2. Click **Settings** → **Branches**
3. Click **Add branch protection rule**
4. Configure:
   - **Branch name pattern**: `main`
   - ✅ **Require status checks to pass before merging**
   - Search and select: `test`
   - ✅ **Require branches to be up to date before merging**
5. Click **Create** or **Save changes**

Now all pull requests to `main` must pass tests before merging!

## Next Steps

- Push your code to GitHub
- The GitHub Actions workflow will run automatically
- Set up branch protection rules as described above
- Create a test pull request to verify the setup

That's it! You now have automated testing with CI/CD 🎉
