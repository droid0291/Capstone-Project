package com.capstone.designpatterntutorial

import org.junit.runners.model.InitializationError

class RobolectricGradleTestRunner(klass: Class<*>?) : RobolectricTestRunner(klass) {
    protected fun getAppManifest(config: Config?): AndroidManifest? {
        val moduleRoot = getModuleRootPath(config)
        val androidManifestFile: FsFile = FileFsFile.from(moduleRoot, "manifests/full/debug/AndroidManifest.xml")
        val resDirectory: FsFile = FileFsFile.from(moduleRoot, "res\\merged\\debug")
        val assetsDirectory: FsFile = FileFsFile.from(moduleRoot, "assets\\debug")
        return AndroidManifest(androidManifestFile, resDirectory, assetsDirectory)
    }

    private fun getModuleRootPath(config: Config?): String? {
        val moduleRoot: String = config.constants().getResource("").toString().replace("file:", "")
        return moduleRoot.substring(0, moduleRoot.indexOf("/classes"))
    }

    companion object {
        private val BUILD_OUTPUT: String? = "intermediates"
    }
}