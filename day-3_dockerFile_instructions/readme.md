# 🐳 Dockerfile Guide

This guide helps you understand and use a `Dockerfile` to containerize your application.

---

## 🧱 What is a Dockerfile?

A `Dockerfile` is a script that contains a set of instructions on how to build a Docker image. It specifies the base image, installs dependencies, copies files, and sets up the app environment.

---

## 🗂️ Basic Dockerfile Instructions

| Instruction | Purpose |
|-------------|---------|
| `FROM`      | Sets the base image (e.g., `python:3.10`, `ubuntu:20.04`) |
| `WORKDIR`   | Sets the working directory inside the container |
| `COPY`      | Copies files/folders from host to container |
| `RUN`       | Executes commands (e.g., installing packages) |
| `CMD`       | Sets the default command when the container starts |
| `EXPOSE`    | Documents the port your container listens on |
| `ENV`       | Sets environment variables |

---

## 📄 Example: Python App Dockerfile

```Dockerfile
# 1. Use an official Python base image
FROM python:3.10-slim

# 2. Set working directory inside container
WORKDIR /app

# 3. Copy requirements.txt and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 4. Copy the app code into the container
COPY . .

# 5. Expose the port the app runs on
EXPOSE 5000

# 6. Command to run the app
CMD ["python", "app.py"]
```
---

## 🚀 Build and Run
```bash
# Build the Docker image (replace "myapp" with your app name)
docker build -t myapp .

# Run a container from the image
docker run -p 5000:5000 myapp
```

## 💡 Tips

- Use a `.dockerignore` file to exclude unnecessary files (e.g., `.git`, `__pycache__`, `venv`).
- For Flask or FastAPI apps, use `flask run` or `uvicorn` in the `CMD`.
- Combine multiple `RUN` commands with `&&` to reduce image layers.



