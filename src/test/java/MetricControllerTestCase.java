import controller.MetricController;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MetricControllerTestCase {
    @Configuration
    static class ContextConfiguration {
        @Bean
        public HashMap<String, List> metrics() {
            return new HashMap<>();
        }
    }

    @Autowired
    private HashMap<String, List> metrics;

    private MetricController sut;

    @Before
    public void setUp() {
        sut = new MetricController();
        sut.setMetrics(metrics);
    }

    @Test
    public void testMetrics() {
        Assert.assertNotNull(sut.getMetrics());
    }

    @Test
    public void testCreateWithValidName() {
        String name = "Valid Name";
        String result = sut.create(name);
        JSONObject jsonObject = new JSONObject(result);

        Assert.assertTrue(jsonObject.has("success"));
    }

    @Test
    public void testCreateWithInvalidName() {
        String name = "ThisNameIsFiftyCharactersLong111111111111111111111";
        String result = sut.create(name);
        JSONObject jsonObject = new JSONObject(result);

        Assert.assertTrue(jsonObject.has("error"));
    }

    @Test
    public void testCreateWithDuplicateName() {
        String name = "Duplicate Name";
        sut.create(name);

        String result = sut.create(name);
        JSONObject jsonObject = new JSONObject(result);

        Assert.assertTrue(jsonObject.has("error"));
    }

    @Test
    public void testAddSingleValue() {
        String name = "Metric";
        Double value = 1.5;

        String result = sut.addValueToMetric(name, value);
        JSONObject jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("error"));
        Assert.assertEquals(jsonObject.get("error"), "Metric with name " + name + " does not exist");

        sut.create(name);
        result = sut.addValueToMetric(name, value);
        jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("success"));
    }

    @Test
    public void testSortAfterAdd() {
        String name = "Metric";
        sut.create(name);

        Double val1 = 30.5;
        Double val2 = 10.9;
        Double val3 = 15.2;
        Double val4 = 11.3;

        sut.addValueToMetric(name, val1);
        sut.addValueToMetric(name, val2);
        sut.addValueToMetric(name, val3);
        sut.addValueToMetric(name, val4);

        HashMap map = sut.getMetrics();
        List values = (List) map.get(name);
        Assert.assertEquals(values.get(0), val2);
        Assert.assertEquals(values.get(1), val4);
        Assert.assertEquals(values.get(2), val3);
        Assert.assertEquals(values.get(3), val1);
    }

    @Test
    public void testGetMean() {
        String name = "Metric";
        Double val1 = 5.0;
        Double val2 = 6.5;
        Double val3 = 10.3;
        Double val4 = 1.25;

        String result = sut.getMean(name);
        JSONObject jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("error"));
        Assert.assertEquals(jsonObject.get("error"), "Metric with name " + name + " does not exist");

        sut.create(name);

        result = sut.getMean(name);
        jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("error"));
        Assert.assertEquals(jsonObject.get("error"), "Metric " + name + " is empty");

        sut.addValueToMetric(name, val1);
        sut.addValueToMetric(name, val2);
        sut.addValueToMetric(name, val3);
        sut.addValueToMetric(name, val4);

        result = sut.getMean(name);
        jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("success"));
        Assert.assertEquals(jsonObject.getDouble("success"), (val1 + val2 + val3 + val4) / 4, .001);
    }

    @Test
    public void testGetMedian() {
        String name = "Metric";
        Double val1 = 5.0;
        Double val2 = 6.5;
        Double val3 = 10.3;
        Double val4 = 1.25;
        Double val5 = 3.75;

        String result = sut.getMedian(name);
        JSONObject jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("error"));
        Assert.assertEquals(jsonObject.get("error"), "Metric with name " + name + " does not exist");

        sut.create(name);

        result = sut.getMedian(name);
        jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("error"));
        Assert.assertEquals(jsonObject.get("error"), "Metric " + name + " is empty");

        sut.addValueToMetric(name, val1);
        sut.addValueToMetric(name, val2);
        sut.addValueToMetric(name, val3);
        sut.addValueToMetric(name, val4);

        result = sut.getMedian(name);
        jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("success"));
        Assert.assertEquals(jsonObject.getDouble("success"), (val1 + val2) / 2, .001);

        sut.addValueToMetric(name, val5);

        result = sut.getMedian(name);
        jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("success"));
        Assert.assertEquals(jsonObject.getDouble("success"), val1, .001);
    }

    @Test
    public void testGetMin() {
        String name = "Metric";
        Double val1 = 5.0;
        Double val2 = 6.5;
        Double val3 = 10.3;
        Double val4 = 1.25;

        String result = sut.getMin(name);
        JSONObject jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("error"));
        Assert.assertEquals(jsonObject.get("error"), "Metric with name " + name + " does not exist");

        sut.create(name);

        result = sut.getMin(name);
        jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("error"));
        Assert.assertEquals(jsonObject.get("error"), "Metric " + name + " is empty");

        sut.addValueToMetric(name, val1);
        sut.addValueToMetric(name, val2);
        sut.addValueToMetric(name, val3);
        sut.addValueToMetric(name, val4);

        result = sut.getMin(name);
        jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("success"));
        Assert.assertEquals(jsonObject.getDouble("success"), val4, .001);
    }

    @Test
    public void testGetMax() {
        String name = "Metric";
        Double val1 = 5.0;
        Double val2 = 6.5;
        Double val3 = 10.3;
        Double val4 = 1.25;

        String result = sut.getMax(name);
        JSONObject jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("error"));
        Assert.assertEquals(jsonObject.get("error"), "Metric with name " + name + " does not exist");

        sut.create(name);

        result = sut.getMax(name);
        jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("error"));
        Assert.assertEquals(jsonObject.get("error"), "Metric " + name + " is empty");

        sut.addValueToMetric(name, val1);
        sut.addValueToMetric(name, val2);
        sut.addValueToMetric(name, val3);
        sut.addValueToMetric(name, val4);

        result = sut.getMax(name);
        jsonObject = new JSONObject(result);
        Assert.assertTrue(jsonObject.has("success"));
        Assert.assertEquals(jsonObject.getDouble("success"), val3, .001);
    }

}
