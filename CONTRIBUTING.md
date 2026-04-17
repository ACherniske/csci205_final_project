# Contributing Guide

## Prerequisites
- **Java JDK**
- **IntelliJ IDEA**
- **Git**

## Cloning the Repository
Open your terminal and run the following commands:

```bash
# Clone the repository
git clone <YOUR_REPO_URL_HERE>

# Navigate into the project directory
cd <YOUR_PROJECT_NAME>
```

## Setting Up Checkstyle (IntelliJ)
We use Checkstyle to maintain code quality. Please configure IntelliJ to use our project's specific ruleset:

1. Go to **Settings > Tools > Checkstyle**.
2. Set the **Scan Scope** to *Only Java sources (including tests)*.
3. In the **Configuration File** section, click the **+** (plus) icon.
   - **Description:** CSCI 205 Checkstyle
   - Select **Use a URL** and paste:
     `https://eg.bucknell.edu/~csci205/2026-spring/checkstyle.xml`
4. Click **Next**, then **Finish**.
5. **Important:** Check the box next to "CSCI 205 Checkstyle" in the list to make it the *Active* configuration.

## Development Workflow

### Conventional Commits
We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification. This helps in generating automated changelogs and managing versions.

**Format:**
```
<type>: <description>
```

**Common Types:**
- `feat`: A new feature for the user
- `fix`: A bug fix
- `docs`: Documentation only changes
- `style`: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
- `refactor`: A code change that neither fixes a bug nor adds a feature
- `test`: Adding missing tests or correcting existing tests
- `chore`: Updating build tasks, package manager configs, etc

**Example:**
```bash
git commit -m "feat: add user authentication logic"
git commit -m "fix: resolve null pointer in student parser"
```

## Branching Process

**Pull latest changes:**
```bash
git checkout main
git pull origin main
```

**Create a feature branch:**
```bash
git checkout -b feature/your-feature-name
```

**Commit & Push:**
Ensure Checkstyle passes before committing.
```bash
git add .
git commit -m "type: description"
git push origin feature/your-feature-name
```

## Coding Standards
- **Checkstyle:** Ensure the Checkstyle tool window shows 0 errors before submitting a Pull Request.
- **Formatting:** Use IntelliJ's "Reformat Code" (`Ctrl+Alt+L` or `Cmd+Option+L`) frequently.
- **Documentation:** Provide Javadoc for all public methods and classes.