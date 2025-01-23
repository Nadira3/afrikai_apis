# Contributing to AfrikAI APIs

## Welcome Contributors! 🌍🚀

Thank you for your interest in contributing to the AfrikAI APIs project. This document provides guidelines to help you contribute effectively and smoothly.

## Table of Contents
1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Process](#development-process)
4. [Contribution Workflow](#contribution-workflow)
5. [Code Quality Standards](#code-quality-standards)
6. [Reporting Issues](#reporting-issues)
7. [Pull Request Process](#pull-request-process)
8. [Community](#community)

## Code of Conduct

We are committed to providing a friendly, safe, and welcoming environment for all contributors. Our project adheres to a [Code of Conduct](CODE_OF_CONDUCT.md) that we expect all participants to read and follow.

## Getting Started

### Prerequisites
- Java 17 or later
- Maven 3.8+
- Git
- Your preferred IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Repository Setup
1. Fork the repository: https://github.com/Nadira3/afrikai_apis
2. Clone your forked repository
   ```bash
   git clone https://github.com/Nadira3/afrikai_apis.git
   cd afrikai_apis
   ```
3. Create a new branch for your contribution
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Development Process

### Environment Setup
1. Ensure all dependencies are installed:
   ```bash
   mvn clean install
   ```
2. Run tests to verify your setup:
   ```bash
   mvn test
   ```

### Coding Standards
- Follow Spring Boot and Java best practices
- Use meaningful variable and method names
- Write clean, documented, and maintainable code
- Adhere to the existing code style in the project

### Dependency Management
- Add new dependencies via Maven
- Ensure compatibility with existing project dependencies
- Update `pom.xml` with appropriate version constraints

## Contribution Workflow

### Finding Issues to Work On
- Check the GitHub Issues section
- Look for issues tagged with:
  - `good first issue`
  - `help wanted`
  - `beginner-friendly`

### Making Changes
1. Create a new branch from `main`
2. Make your changes
3. Write or update tests
4. Ensure all tests pass
5. Document your changes

### Commit Guidelines
- Use clear and descriptive commit messages
- Format: `[Type]: Concise description`
  - Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
- Example: `feat: Add user authentication for Label Service`

## Code Quality Standards

### Static Code Analysis
- Use checkstyle for code formatting
- Run `mvn checkstyle:check` before submitting
- Resolve all linting warnings

### Testing
- Write unit tests for new functionality
- Aim for >80% test coverage
- Use JUnit and Mockito for testing
- Ensure integration tests are comprehensive

### Documentation
- Update README.md if needed
- Add Javadoc comments for public methods
- Include inline comments for complex logic

## Reporting Issues

### Bug Reports
- Use GitHub Issues template
- Provide:
  - Clear description
  - Steps to reproduce
  - Expected vs. actual behavior
  - Environment details

### Feature Requests
- Describe the proposed feature
- Explain its value to the project
- Provide potential implementation approach

## Pull Request Process

1. Ensure your code passes all tests
2. Update documentation
3. Include a clear description of changes
4. Reference related issues
5. Wait for code review

### Review Process
- At least one maintainer must approve
- Automated checks must pass
- Address review comments promptly

## Community

### Communication Channels
- GitHub Discussions
- Email: paitanun35@gmail.com

### Ways to Contribute
- Code improvements
- Documentation
- Bug reporting
- Feature suggestions
- Community support

## Recognition

Contributors will be recognized in:
- PROJECT_CONTRIBUTORS.md
- GitHub repository contributors graph
- Project documentation

## License

By contributing, you agree that your contributions will be licensed under the project's existing license.

---

**Happy Coding! 💻🌍**

Maintained by the AfrikAI Development Team
