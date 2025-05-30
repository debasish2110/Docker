pipeline {
    agent any
    environment  {
        AWS_ACCOUNT_ID = credentials('ACCOUNT_ID')
        AWS_ECR_REPO_NAME = credentials('ecr_repo')
        AWS_DEFAULT_REGION = 'us-east-1'
        REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/"
    }
    
    stages {
        stage('Clone repository') { 
            steps { 
                git branch: 'main', url: 'https://github.com/CloudTechDevOps/project-1-maven-jenkins-CICD-docker-eks-.git'
            }
        }
        stage('maven install') { 
            steps { 
                sh 'mvn install'
            }
        }
        stage('docker Build') { 
            steps { 
                sh '''
                        docker system prune -f
                        docker build -t ${AWS_ECR_REPO_NAME} .
                        '''
                
            }
        }
        stage('image push to ecr') {
            steps {
                sh '''
                aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${REPOSITORY_URI}
                docker tag ${AWS_ECR_REPO_NAME} ${REPOSITORY_URI}${AWS_ECR_REPO_NAME}:${BUILD_NUMBER}
                docker push ${REPOSITORY_URI}${AWS_ECR_REPO_NAME}:${BUILD_NUMBER}
                '''
            }
        }
            
        stage('Update Deployment file') {
            environment {
                GIT_REPO_NAME = "project-1-maven-jenkins-CICD-docker-eks-"
                GIT_USER_NAME = "CloudTechDevOps"
            }
            steps {
                dir('Kubernetes-Manifests-file') {
                    withCredentials([string(credentialsId: 'git_hub', variable: 'git_token')]) {
                        sh '''
                            git config user.email "cloud87777@gmail.com"
                            git config user.name "CloudTechDevOps"
                            BUILD_NUMBER=${BUILD_NUMBER}
                            echo $BUILD_NUMBER
                            sed -i "s#image:.*#image: $REPOSITORY_URI$AWS_ECR_REPO_NAME:$BUILD_NUMBER#g" deploy_svc.yml
                            git add .
                            git commit -m "Update deployment Image to version \${BUILD_NUMBER}"
                            git push https://${git_token}@github.com/${GIT_USER_NAME}/${GIT_REPO_NAME} HEAD:main
                            '''
                    }
                }
            }
        }
    }
}

