package biz.lermitage.sub

import biz.lermitage.sub.conf.LocalAppConf
import biz.lermitage.sub.model.SoftwareUpdate
import biz.lermitage.sub.service.checker.Checker
import biz.lermitage.sub.service.report.JsonReportLoader
import biz.lermitage.sub.service.report.Reporter
import biz.lermitage.sub.service.report.UpdatesMerger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(LocalAppConf::class)
class SoftwareUpdatesBotApplication : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var checkers: List<Checker>

    @Autowired
    lateinit var reporters: List<Reporter>

    @Autowired
    lateinit var jsonReportLoader: JsonReportLoader

    @Autowired
    lateinit var updatesMerger: UpdatesMerger

    override fun run(vararg args: String?) {
        val latestUpdates = ArrayList<SoftwareUpdate>()

        checkers.forEach { checker: Checker ->
            try {
                val check = checker.check()
                Thread.sleep(1000)
                logger.info("fetched $check")
                latestUpdates.add(check)
            } catch (e: Exception) {
                logger.warn("checker ${checker::class.java} failed, ignoring", e)
            }
        }

        val previousReport = jsonReportLoader.load()
        val mergedUpdates = updatesMerger.merge(previousReport.updates, latestUpdates)

        reporters.forEach { reporter: Reporter ->
            try {
                reporter.generate(mergedUpdates)
            } catch (e: Exception) {
                logger.warn("reporter ${reporter::class.java} failed, ignoring", e)
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<SoftwareUpdatesBotApplication>(*args)
}