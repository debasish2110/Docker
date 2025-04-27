pipeline {
    agent any

    stages{
        stage('clone repository'){
            steps{
                git branch: 'main', credentialsId: 'git', url: 'https://github.com/debasish2110/JavaCode_for_docker_maven_jenkins_practice.git'
            }
        }

        stage('maven install'){
            steps{
                sh 'mvn install'
            }
        }

        stage('build'){
            steps{
                sh '''
                    docker build -t demo .
                    docker rm -f $(docker ps -aq) || true
                    docker run -dt -p 8081:80 demo
                    docker system prune -a
                   '''
            }
        }

        stage('image push to ECR'){
            steps{
                sh '''
                aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 709524548272.dkr.ecr.ap-south-1.amazonaws.com
                docker tag demo:latest 709524548272.dkr.ecr.ap-south-1.amazonaws.com/demo:latest
                docker push 709524548272.dkr.ecr.ap-south-1.amazonaws.com/demo:latest
                '''
            }
        }
    }
}