FROM python:3-slim

WORKDIR /programas/api-users

COPY requirements.txt .
RUN pip install -r requirements.txt && \
    rm -rf /root/.cache/pip

COPY . .

EXPOSE 8000

CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]