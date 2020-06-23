# enviornment deps
FROM clojure:openjdk-14-tools-deps-1.10.1.502-buster as deps
WORKDIR /app
COPY deps.edn .
RUN clojure -A:uberjar:test:runner -Stree

# deps + code
FROM deps as deps-with-code
WORKDIR /app
COPY . .

# build
FROM deps-with-code as builder
WORKDIR /app
RUN clojure -Spom
RUN clojure -A:uberjar

# final
FROM deps
EXPOSE 80
WORKDIR /app
RUN mkdir -p storage
COPY --from=builder /app/api.jar ./
CMD ["java", "-jar", "api.jar"]