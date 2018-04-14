package $package$.services

import java.time.Instant

import $package$.services.MetricsService.ApplicationMetrics
import kamon.metric.MeasurementUnit.{Dimension, Magnitude}
import kamon.metric._
import org.scalatest.Assertion

class MetricsSpec extends BaseSpec {

  "Metrics Service" should "return Nil for all metrics before it has been initialized" in {
    val metricsSvc = new KamonMetricsService
    metricsSvc.allMetrics shouldBe ApplicationMetrics(Nil, Nil, Nil)
  }

  it should "convert from Kamon format to standard metrics format" in {
    val metricsSvc = new KamonMetricsService

    val now = Instant.now
    val kamonMetrics = MetricsSnapshot(
      histograms = Seq(sampleMetricDistribution),
      rangeSamplers = Seq(sampleMetricDistribution),
      gauges = Seq(sampleMetricValue),
      counters = Seq(sampleMetricValue)
    )
    val kamonSnapshot = PeriodSnapshot(now, now, kamonMetrics)
    metricsSvc.reportPeriodSnapshot(kamonSnapshot)

    val Seq(histogram, span) = metricsSvc.allMetrics.timers
    val Seq(counter)         = metricsSvc.allMetrics.counters
    val Seq(gauge)           = metricsSvc.allMetrics.gauges

    validateTimer(histogram)
    validateTimer(span)

    validateCounter(counter)

    validateGauge(gauge)
  }

  private def validateTimer(timer: MetricsService.Timer): Assertion = {
    timer.name shouldBe "metric distribution"
    timer.min shouldBe 100
    timer.max shouldBe 1000
    timer.mean shouldBe 250
    timer.count shouldBe 10
    timer.tags shouldBe Map("key" -> "value")
    timer.percentiles.p50 shouldBe 666
    timer.percentiles.p75 shouldBe 666
    timer.percentiles.p95 shouldBe 666
    timer.percentiles.p98 shouldBe 666
    timer.percentiles.p99 shouldBe 666
    timer.percentiles.p999 shouldBe 666
  }

  private def validateCounter(counter: MetricsService.Counter): Assertion = {
    counter.name shouldBe "metric value"
    counter.tags shouldBe Map("key" -> "value")
    counter.count shouldBe 145
  }

  private def validateGauge(gauge: MetricsService.Gauge): Assertion = {
    gauge.name shouldBe "metric value"
    gauge.tags shouldBe Map("key" -> "value")
    gauge.value shouldBe 145
  }

  val sampleMesurementUnit: MeasurementUnit = MeasurementUnit(Dimension("tps"), Magnitude("", 1L))

  val samplePercentile: Percentile = new Percentile {

    override def countUnderQuantile: Long = 10

    override def quantile: Double = 99.9

    override def value: Long = 666
  }

  val sampleMetricDistribution: MetricDistribution = MetricDistribution(
    name = "metric distribution",
    tags = Map("key" -> "value"),
    unit = sampleMesurementUnit,
    dynamicRange = DynamicRange(1, 1, 1),
    distribution = new Distribution {

      override def percentiles: Seq[Percentile] = Seq(samplePercentile)

      override def max: Long = 1000L

      override def buckets: Seq[Bucket] = Nil

      override def count: Long = 10

      override def bucketsIterator: Iterator[Bucket] = Nil.iterator

      override def sum: Long = 2500L

      override def percentilesIterator: Iterator[Percentile] = percentiles.iterator

      override def percentile(p: Double): Percentile = samplePercentile

      override def min: Long = 100L
    }
  )

  val sampleMetricValue: MetricValue = MetricValue(
    name = "metric value",
    tags = Map("key" -> "value"),
    unit = sampleMesurementUnit,
    value = 145L
  )
}
