workspace "Tech-testing-ground" {

    !identifiers hierarchical
    !impliedRelationships false

    !docs docs

    model {

        // general model

        user = person "User"

        jira = softwareSystem "Jira" {
            description "Platform for project-management."
            ui = container "Single Page Application" {
                description "Provides all functionality of the platform via a graphical interface in the browser. Provides external system the option to visually integrate via iframes."
            }
            webApp = container "WebApp" {
                description "Delivers the assests of the Client Side Application. Provides an API for the core-functionality. Provides webhook-integration to external systems."
                -> ui "Serves" "" "container-dependency"
                ui -> this "Makes API-calls to" "" "container-dependency"
            }
            user -> ui "Interacts with"
        }

        monday = softwareSystem "Monday" {
            description "Platform for project-management."
            ui = container "Single Page Application" {
                description "Provides all functionality of the platform via a graphical interface in the browser. Provides external system the option to visually integrate via iframes."
            }
            webApp = container "WebApp" {
                description "Delivers the assests of the Client Side Application. Provides an API for the core-functionality. Provides webhook-integration to external systems."
                -> ui "Serves" "" "container-dependency"
                ui -> this "Makes API-calls to" "" "container-dependency"
            }
            user -> ui "Interacts with"
        }

        mondayJiraSync = softwareSystem "MondayJiraSync" {
            !adrs decisions/general
            description "Application that enables users to synchronize items between their boards in the Jira and Monday platform."
            webApp = container "WebApp" {
                description "Delivers the assests of the Client Side Applications to the external systems. Provides core-functionality via an API. Is subscribed to webhooks of connected platforms."
                db = component "Database Facade" {
                    description "Provides application-specific API for database."
                }
                core = component "Core" {
                    description "Provides all core-functionality in an platform-independent way.
                    -> db "Accesses database via"
                }
                api = component "API" {
                    description "Provides an interface to the core-functionality for external systems"
                    -> core "Uses to fulfill requests"
                }
                jiraIntegration = component "Jira-integration" {
                    description "Encapsulates functionality for integration with Jira."
                    -> jira.webApp "Enables integration with"
                    core -> this "Integrates with platform via"
                }
                mondayIntegration = component "Monday-integration" {
                    description "Encapsulates functionality for integration with Monday."
                    -> monday.webApp "Enables integration with"
                    core -> this "Integrates with platform via"
                }
                -> jira.webApp "Makes API-calls to" "" "container-dependency"
                -> monday.webApp "Makes API-calls to" "" "container-dependency"
                jira.webApp -> this "Makes webhook call-backs to" "" "container-dependency"
                monday.webApp -> this "Makes webhook call-backs to" "" "container-dependency"
                jira.ui -> this "Requests iframe-content" "" "container-dependency"
                monday.ui -> this "Requests iframe-content" "" "container-dependency"
            }
            db = container "Database" {
                description "Stores application data."
                webApp -> this "Reads from and writes to"
            }

            uiWebsite = container "Client Side Application Website" {
                description "Enables users to view and manipulate all of their board- and item-links via an admin-website."
                webApp -> this "Serves" "" "container-dependency"
                this -> webApp "Makes API-calls to" "" "container-dependency"
                -> webApp.api "Communicates with"
            }

            uiJiraIntegration = container "Client Side Application Jira-integration" {
                description "Enables users to view and manipulate a limited subset of their item-links based on the item-view it's rendered in."
                -> jira.ui "Is rendered inside" "iframe" "container-dependency"
                webApp -> this "Serves" "" "container-dependency"
                this -> webApp "Makes API-calls to" "" "container-dependency"
                -> webApp.api "Communicates with"
            }

            uiMondayIntegration = container "Client Side Application Monday-integration" {
                description "Enables users to view and manipulate a limited subset of their item links based on the integration point."
                -> monday.ui "Is rendered inside" "iframe" "container-dependency"
                webApp -> this "Serves" "" "container-dependency"
                this -> webApp "Makes API-calls to" "" "container-dependency"
                -> webApp.api "Communicates with"
            }
        }

        user -> mondayJiraSync "Configures synchronisation"
        user -> Jira "Manages project in"
        user -> Monday "Manages project in"
        mondayJiraSync -> monday "Synchronizes"
        mondayJiraSync -> jira "Synchronizes"

        // deployment

        deploymentEnvironment Live {
            deploymentNode "Google Cloud Platform" {
                tags "Cloud Platform"
                deploymentNode "Cloud Run" {
                    tags "Google Cloud Platform - Cloud Run"

                    deploymentNode "Docker Container" {
                        tags "Docker Container"
                        containerInstance mondayJiraSync.webApp
                    }

                }
                deploymentNode "Firestore" {
                    tags "Google Cloud Platform - Cloud Firestore"
                    dbInstance = containerInstance mondayJiraSync.db
                }
            }
            deploymentNode "User's computer" {
                deploymentNode "Web Browser" {
                    containerInstance mondayJiraSync.uiWebsite
                    containerInstance mondayJiraSync.uiJiraIntegration
                    containerInstance mondayJiraSync.uiMondayIntegration
                }
            }
        }
    }

    views {

        systemContext mondayJiraSync high-level-static-overview {
            description "High-level overview of the application in relation to the platforms it interacts with"
            include MondayJiraSync Jira Monday User
        }

        dynamic * high-level-dynamic-data-synchronisation {
            description "High-level overview of the steps involved for synchronisation"
            user -> jira "Performs action in"
            jira -> mondayJiraSync "Notifies about update"
            mondayJiraSync -> monday "Synchronizes update in"
            monday -> user "Shows updated state to"
        }

        container mondayJiraSync container-system-overview {
            include * element.parent==jira element.parent==monday
            exclude mondayJiraSync.db user
        }

        filtered container-system-overview include Element,container-dependency container-system-overview-dependency-overview "Overview of the dependencies between the various containers in the systems. Not exhaustive, some dependencies are leftout or bundled for clearity"

        dynamic mondayJiraSync container-monday-jira-sync-focus-update-synchronisation {
            description "Overview of the steps involved for synchronisation on the container-level"
            user -> jira.ui "Performs action in"
            jira.ui -> jira.webApp "Forwards action to"
            jira.webApp -> mondayJiraSync.webApp "Notifies about update"
            mondayJiraSync.webApp -> monday.webApp "Syncronizes update in"
            monday.webApp -> monday.ui "Pushes update to"
            monday.ui -> user "Shows updated state to"
        }

        component mondayJiraSync.webApp component-monday-jira-sync-web-app {
            description "Overview of the mondayJiraSync-artifacts for a discussion about which parts are generic or unique per prototype"
            include element.parent==mondayJiraSync.webApp
        }

        component mondayJiraSync.webApp component-monday-jira-sync-web-app-focus-unique-for-prototypes {
            description "Overview of the mondayJiraSync-artifacts for a discussion about which parts are generic or unique per prototype"
            include element.parent==mondayJiraSync.webApp mondayJiraSync.uiMondayIntegration mondayJiraSync.uiJiraIntegration mondayJiraSync.uiWebsite
        }

        dynamic mondayJiraSync.webApp component-monday-jira-sync-focus-update-synchronisation {
            description "Overview of the steps involved for synchronisation on the component-level of the mondayJiraSync webApp"
            jira.webApp -> mondayJiraSync.webApp.jiraIntegration "Notifies about update"
            mondayJiraSync.webApp.jiraIntegration -> mondayJiraSync.webApp.core "Notifies about update"
            mondayJiraSync.webApp.core -> mondayJiraSync.webApp.db "Loads synchronisation configuration via"
            mondayJiraSync.webApp.core -> mondayJiraSync.webApp.mondayIntegration "Propagates update to"
            mondayJiraSync.webApp.mondayIntegration -> monday.webApp "Synchronizes update in"
        }

        deployment * Live {
            include *
        }

        styles {
            element "Element" {
                description false
            }
            element "Docker Container" {
                stroke #4285f4
                color #4285f4
            }
            element "Cloud Platform" {
                stroke #4285f4
                color #4285f4
            }
        }

        theme default
        theme https://static.structurizr.com/themes/google-cloud-platform-v1.5/theme.json
    }

}
