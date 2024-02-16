# Amber Toolchain  
Main toolchain used by Amber platform.  

## Getting Started  
1) Create a new Gradle project and add following code to `settings.gradle.kts`:
    ```kts
    // settings.gradle.kts
    pluginManagement {
        repositories {
            gradlePluginPortal()
        }
    }
    ```
2) Import the plugin in your `build.gradle.kts`:
    ```kts
    // build.gradle.kts
    plugins {
        id("lol.koblizek.amber-toolchain") version "0.1.0" // Check for new versions regularly
    }
    ```
3) Reload the project and configure the plugin:
    ```kts
    // build.gradle.kts
    minecraft {
        version("1.16.5")
        mappings("official")
    }
    amber {
        // Leave blank for disabled development mode
    }
    ```
4) Done!

### Development mode
Development mode allows you to decompile Minecraft into specified directories.

## Contributing 
Standard contribution guidelines apply.
Clone the repository and create a new branch for your feature. 
When you are done, create a pull request and wait for approval.

## Licensing  
This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details.