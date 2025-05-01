FROM python:3-slim
WORKDIR /programas/api-users
RUN pip3 install "fastapi[standard]"
RUN pip3 install pydantic
RUN pip3 install mysql-connector-python
COPY . .
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]

