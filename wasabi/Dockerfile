# Use a lightweight Java runtime (adjust 17 to your Java version)
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file into the image
# Make sure to check the 'target' folder for the exact name of your jar
COPY target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]