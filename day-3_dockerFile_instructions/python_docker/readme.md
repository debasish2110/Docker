# Dockerfile Instructions Explained
---

## 1. **`FROM python:3.12`**

This specifies the base image to use. In this case, it's an official Python 3.12 image.

The base image includes Python 3.12 and essential dependencies, saving you from installing Python from scratch.

---

## 2. **`MAINTAINER deba "debasishdash98@gmail.com"`**

This instruction was used to specify the maintainer of the Docker image. It’s now deprecated, but you can still include it for documentation purposes. It identifies who created and maintains the image.

The correct approach today is to use labels like:

```Dockerfile
LABEL maintainer="debasishdash98@gmail.com"
```

## 3. **`COPY . /app`**

This copies all files from the current directory (where the Dockerfile is) on the host machine to the `/app` directory in the Docker container.

- The `.` represents the current directory.
- `/app` is the target directory inside the container.

## 4. **`WORKDIR /app`**

This sets the working directory inside the container to `/app`.

Any subsequent instructions that use relative paths (like `RUN`, `COPY`, etc.) will use `/app` as the base directory.

---

## 5. **`RUN pip install -r requirements.txt`**

This installs the Python dependencies listed in the `requirements.txt` file inside the container using `pip`.

It's executed during the image build process, so the dependencies will be available in the final image.

## 6. **`ENTRYPOINT [ "python3" ]`**

This defines the default executable that will be run when the container starts.

`ENTRYPOINT` makes the container run `python3` as the main process when the container starts.

This means every time you run the container, `python3` will be invoked.

---

## 7. **`CMD [ "app.py" ]`**

This provides the default argument to the executable defined in the `ENTRYPOINT`.

In this case, it will run `python3 app.py` by default when the container starts.

If you override the command when running the container, this will be replaced.

---

## 🧩 How They Work Together:

- `ENTRYPOINT` defines the program to run (in this case, `python3`), while `CMD` provides the default argument (`app.py`).
- Together, they make the container execute `python3 app.py` by default.
- If you provide a different command while running the container, the `CMD` will be overridden but `ENTRYPOINT` will stay intact.
