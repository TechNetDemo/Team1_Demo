pipeline {
    agent none
    stages{
        stage('start_approval'){
            steps{
            input message: 'ready to start?', submitter: 'admin, prod_admin'
            }
        }
        
        stage('Prerequisite'){
            steps{
            echo '<Deploy DB, tomcat, etc.>'
            }
        }
        stage('WebApp'){
            steps{
            build job: '/TopLevelPipelineDemo/PROD/Team1_PROD_DownloadWebApp', parameters: [string(name: 'artifact_version', value: artifact_version)]
            build job: '/TopLevelPipelineDemo/PROD/Team1_PROD_ReplaceWebAppToken', parameters: [string(name: 'db_username', value: db_username), string(name: 'db_password', value: db_password), string(name: 'db_url', value: db_url), string(name: 'db_tableName', value: db_tableName), string(name: 'artifact_version', value: artifact_version)]
            build job: '/TopLevelPipelineDemo/PROD/Team1_PROD_DeployWebApp', parameters: [[$class: 'NodeParameterValue', name: 'node_to_run', allNodesMatchingLabel: true,labels: [node_to_run], nodeEligibility: [$class: 'AllNodeEligibility'], triggerIfResult: 'allCases']]

            }
        }

    }
    post {
        failure {
            echo '<rollback process>'
        }
    }
    
}