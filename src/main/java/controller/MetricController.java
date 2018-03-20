package controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Controller
public class MetricController {
    @Autowired
    private HashMap<String, List> metrics;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@RequestParam(name = "name") String name) {
        JSONObject toReturn = new JSONObject();

        if (metrics.containsKey(name)) {
            toReturn.put("error", "Metric with name " + name + " already exists");
            return toReturn.toString();
        }

        if (!nameIsValid(name)) {
            toReturn.put("error", "Metric " + name + " is invalid");
            return toReturn.toString();
        }

        try {
            ArrayList<Double> values = new ArrayList<>();
            metrics.put(name, values);
            toReturn.put("success", "Metric " + name + " created");
        } catch (Exception e) {
            toReturn.put("error", e.getLocalizedMessage());
        }

        return toReturn.toString();
    }

    @RequestMapping(value = "/addvalue", method = RequestMethod.POST)
    public String addValueToMetric(@RequestParam(name = "name") String name,
                                   @RequestParam(name = "value") Double value) {
        JSONObject toReturn = new JSONObject();

        if (!metrics.containsKey(name)) {
            toReturn.put("error", "Metric with name " + name + " does not exist");
            return toReturn.toString();
        }

        try {
            ArrayList<Double> values = (ArrayList<Double>) metrics.get(name);
            values = insertValueInOrder(values, value);
            metrics.put(name, values);

            toReturn.put("success", "Value " + value + " inserted into metric " + name);
        } catch (Exception e) {
            toReturn.put("error", e.getLocalizedMessage());
        }

        return toReturn.toString();
    }

    @RequestMapping(value = "/getmean/{name}", method = RequestMethod.GET)
    public String getMean(@PathVariable String name) {
        JSONObject toReturn = new JSONObject();

        if (!metrics.containsKey(name)) {
            toReturn.put("error", "Metric with name " + name + " does not exist");
            return toReturn.toString();
        }

        try {
            ArrayList<Double> values = (ArrayList<Double>) metrics.get(name);
            Double sum = 0.0;

            if (values.isEmpty()) {
                toReturn.put("error", "Metric " + name + " is empty");
                return toReturn.toString();
            }

            for (Double entry : values) {
                sum += entry;
            }

            Double mean = sum/values.size();
            toReturn.put("success", mean);
        } catch (Exception e) {
            toReturn.put("error", e.getLocalizedMessage());
        }

        return toReturn.toString();
    }

    @RequestMapping(value = "/getmedian/{name}", method = RequestMethod.GET)
    public String getMedian(@PathVariable String name) {
        JSONObject toReturn = new JSONObject();

        if (!metrics.containsKey(name)) {
            toReturn.put("error", "Metric with name " + name + " does not exist");
        }

        try {
            ArrayList<Double> values = (ArrayList<Double>) metrics.get(name);
            Double median;
            int size = values.size();

            if (values.isEmpty()) {
                toReturn.put("error", "Metric " + name + " is empty");
                return toReturn.toString();
            }

            if ((size % 2) > 0) {
                median = values.get(size/2);
            } else {
                // 0 1 2 3
                median = (values.get(size/2) + values.get((size/2) - 1)) / 2;
            }

            toReturn.put("success", median);
        } catch (Exception e) {
            toReturn.put("error", e.getLocalizedMessage());
        }

        return toReturn.toString();
    }

    @RequestMapping(value = "/getmin/{name}", method = RequestMethod.GET)
    public String getMin(@PathVariable String name) {
        JSONObject toReturn = new JSONObject();

        if (!metrics.containsKey(name)) {
            toReturn.put("error", "Metric with name " + name + " does not exist");
        }

        try {
            ArrayList<Double> values = (ArrayList<Double>) metrics.get(name);

            if (values.isEmpty()) {
                toReturn.put("error", "Metric " + name + " is empty");
                return toReturn.toString();
            }

            toReturn.put("success", values.get(0));
        } catch (Exception e) {
            toReturn.put("error", e.getLocalizedMessage());
        }

        return toReturn.toString();
    }

    @RequestMapping(value = "/getmax/{name}", method = RequestMethod.GET)
    public String getMax(@PathVariable String name) {
        JSONObject toReturn = new JSONObject();

        if (!metrics.containsKey(name)) {
            toReturn.put("error", "Metric with name " + name + " does not exist");
        }

        try {
            ArrayList<Double> values = (ArrayList<Double>) metrics.get(name);
            int size = values.size();

            if (values.isEmpty()) {
                toReturn.put("error", "Metric " + name + " is empty");
                return toReturn.toString();
            }

            toReturn.put("success", values.get(size - 1));
        } catch (Exception e) {
            toReturn.put("error", e.getLocalizedMessage());
        }

        return toReturn.toString();
    }

    private boolean nameIsValid(String name) {
        return name.length() < 50;
    }

    private ArrayList insertValueInOrder(ArrayList values,
                                    Double value) {
        values.add(value);
        Collections.sort(values);

        return values;
    }

    public void setMetrics(HashMap metrics) {
        this.metrics = metrics;
    }

    public HashMap getMetrics() {
        return metrics;
    }
}
