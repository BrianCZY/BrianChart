To get a Git project into your build:

Tag = 1.0.8

--------------------------------------------------
gradle:
Step 1. Add the JitPack repository to your build file

gradle
Add it in your root settings.gradle at the end of repositories:

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.BrianCZY:BrianChart:Tag'
	}

------------------------------------------------------

gradle.kts:
Step 1. Add the JitPack repository to your build file

Add it in your settings.gradle.kts at the end of repositories:

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation("com.github.BrianCZY:BrianChart:Tag")
	}