
# Jenkins Pipeline for Building and Pushing Docker Image to AWS ECR

This project automates the process of building a Docker image from a Maven-based Java project and pushing it to AWS Elastic Container Registry (ECR) using a Jenkins pipeline.

## Prerequisites

Before you begin, ensure you have the following set up:

- **Jenkins** installed and configured with a working pipeline.
- **AWS Account** with permissions to access ECR (Elastic Container Registry).
- **GitHub Repository** containing your project’s source code.
- **Maven** installed and configured on your Jenkins agents.
- **Docker** installed and running on the Jenkins agents.

### steps

- create an EC2 instance and install Docker, jenkins and maven in it
- install AWS ECR plugin and docker plugin in jenkins
- in aws ECR create a repo and fetch the push commands and wite in the pipeline
- create an IAM role for EC2 with ECR permission and attach it to the jenkins instance
- before running the pipeline make sure `/var/run/docker.sock` file has `666` rw permissions (give access to docker daemon to run docker server using jenkins)

### 1. Jenkins Configuration

- Ensure Jenkins is configured to execute pipelines.
- Add necessary credentials for GitHub and AWS ECR in Jenkins:
  - **GitHub Credentials**: Used for cloning the GitHub repository. These credentials should be added to Jenkins as "Git" credentials.
  - **AWS Credentials**: The pipeline will use AWS CLI to log into the ECR repository, so make sure AWS credentials are set up on the Jenkins agent or passed through Jenkins.

### 2. Repository Setup

Clone the GitHub repository that contains your Java project and Dockerfile. This repository should have the necessary `Dockerfile` for building the Docker image.

### 3. Pipeline Configuration

This Jenkinsfile is used to automate the following steps:

- Clone the repository from GitHub.
- Run `mvn install` to build the Java application and resolve dependencies.
- Build the Docker image.
- Push the Docker image to AWS ECR.

## Steps in the Pipeline

### 1. Clone Repository
This stage pulls the latest code from the main branch of your GitHub repository.
```groovy
git branch: 'main', credentialsId: 'git', url: 'https://github.com/debasish2110/JavaCode_for_docker_maven_jenkins_practice.git'
```

### 2. Maven Install
This stage runs the `mvn install` command to resolve Maven dependencies and build the application.
```groovy
sh 'mvn install'
```

### 3. Build Docker Image
This stage builds the Docker image using the `Dockerfile` located in the root of the repository. It also runs the image and prunes unused Docker images.
```groovy
sh '''
   docker build -t demo .
   docker rm -f $(docker ps -aq) || true
   docker run -dt -p 8081:80 demo
   docker system prune -a
'''
```

### 4. Image Push to AWS ECR
This stage logs into the AWS ECR repository and pushes the built Docker image.
```groovy
sh '''
   aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 709524548272.dkr.ecr.ap-south-1.amazonaws.com
   docker tag demo:latest 709524548272.dkr.ecr.ap-south-1.amazonaws.com/demo:latest
   docker push 709524548272.dkr.ecr.ap-south-1.amazonaws.com/demo:latest
'''
```

## Configuration Details

### AWS ECR

1. **Repository Setup**:
   - Ensure you have an ECR repository created in your AWS account.
   - Replace `709524548272.dkr.ecr.ap-south-1.amazonaws.com` with the URI of your ECR repository.

2. **Permissions**:
   - Ensure your AWS user or role has sufficient permissions to push images to the ECR repository. Use policies like `AmazonEC2ContainerRegistryFullAccess` for the user/role.

### GitHub

- The pipeline expects the project to be hosted on GitHub and accessible via the repository URL provided in the pipeline.
- You should have a `Dockerfile` in the repository’s root directory.

## How to Run the Pipeline

1. **Set Up Jenkins**:
   - Create a new Jenkins Pipeline job.
   - In the job configuration, point to the GitHub repository containing your Jenkinsfile.

2. **Add Required Credentials**:
   - Add GitHub credentials (`git`) for cloning the repository.
   - Add AWS credentials to Jenkins for interacting with ECR.

3. **Run the Pipeline**:
   - Trigger the pipeline either manually from Jenkins or set up a webhook to automatically trigger the pipeline on GitHub commits.

4. **Monitor the Output**:
   - Monitor the console output to ensure the pipeline runs correctly, builds the Docker image, and pushes it to the AWS ECR repository.

## Troubleshooting

- If the pipeline fails at the `mvn install` stage, ensure that the necessary Maven dependencies and `pom.xml` file are correctly configured.
- If the `docker build` stage fails, verify that the `Dockerfile` is valid and properly defined in the root of the repository.
- If the image push to ECR fails, check the AWS credentials and ensure that the ECR repository exists and is accessible.

## Conclusion

This Jenkins pipeline provides a fully automated workflow for building Docker images from a Maven-based Java project and pushing them to AWS ECR. By using this pipeline, you can streamline the process of building and deploying Docker images to AWS, reducing manual effort and ensuring consistency.
