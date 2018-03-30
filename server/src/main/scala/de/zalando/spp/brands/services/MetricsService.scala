package de.zalando.spp.brands.services

import com.typesafe.config.Config
import kamon.metric.{MetricDistribution, MetricValue, MetricsSnapshot, PeriodSnapshot}
import kamon.{Kamon, MetricReporter, Tags}
import com.typesafe.scalalogging.Logger
import de.zalando.spp.brands.services.MetricsService._
import kamon.system.SystemMetrics

object MetricsService {

  case class Counter(name: String, count: Long, tags: Tags)
  case class Gauge(name: String, value: Long, tags: Tags)
  case class Percentiles(p50: Long, p75: Long, p95: Long, p98: Long, p99: Long, p999: Long)
  case class Timer(name: String, count: Long, max: Long, min: Long, mean: Long, percentiles: Percentiles, tags: Tags)
  case class ApplicationMetrics(counters: Seq[Counter], gauges: Seq[Gauge], timers: Seq[Timer])
}

trait MetricsService {

  def allMetrics: ApplicationMetrics
}

object KamonMetricsService {

  def constructNameWithTags(name: String, tags: Tags): String =
    if (tags.isEmpty) name
    else
      (List(tags.get("component"), Some(name)) ++ tags.filterKeys(_ != "component").values.toList.map(Some(_))).flatten
        .mkString(".")

  def kamonMetricValueToCounter(mv: MetricValue): Counter =
    Counter(mv.name, mv.value, mv.tags)

  def kamonMetricValueToGauge(mv: MetricValue): Gauge =
    Gauge(mv.name, mv.value, mv.tags)

  def kamonMetricDistributionToTimer(md: MetricDistribution): Timer = {
    Timer(
      name = md.name,
      count = md.distribution.count,
      max = md.distribution.max,
      min = md.distribution.min,
      mean = if (md.distribution.count > 0) md.distribution.sum / md.distribution.count else 0L,
      percentiles = Percentiles(
        p50 = md.distribution.percentile(50).value,
        p75 = md.distribution.percentile(75).value,
        p95 = md.distribution.percentile(95).value,
        p98 = md.distribution.percentile(98).value,
        p99 = md.distribution.percentile(99).value,
        p999 = md.distribution.percentile(99.9).value
      ),
      tags = md.tags
    )
  }
}

class KamonMetricsService extends MetricsService with MetricReporter {

  import KamonMetricsService._

  private val logger                      = Logger("KamonMetricsService")
  private var metrics: ApplicationMetrics = ApplicationMetrics(Nil, Nil, Nil)

  Kamon.addReporter(this)

  override def allMetrics: ApplicationMetrics = metrics

  override def reportPeriodSnapshot(snapshot: PeriodSnapshot): Unit = {
    metrics = convertToInternalFormat(snapshot.metrics)
    logger.debug(s"Kamon metrics snapshot recorded for period: ${snapshot.from} to: ${snapshot.to}")
  }

  override def start(): Unit =
    SystemMetrics.startCollecting()
  override def stop(): Unit =
    SystemMetrics.stopCollecting()
  override def reconfigure(config: Config): Unit = ()

  private def convertToInternalFormat(kamonMetrics: MetricsSnapshot): ApplicationMetrics = {
    val counters = kamonMetrics.counters.map(kamonMetricValueToCounter)
    val gauges   = kamonMetrics.gauges.map(kamonMetricValueToGauge)
    val timers = kamonMetrics.histograms.map(kamonMetricDistributionToTimer) ++ kamonMetrics.rangeSamplers.map(
      kamonMetricDistributionToTimer)

    ApplicationMetrics(counters = counters, gauges = gauges, timers = timers)
  }
}
