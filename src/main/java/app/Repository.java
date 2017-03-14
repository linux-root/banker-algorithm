package app;

import app.model.Model;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by chipn@eway.vn on 1/16/17.
 */
public class Repository {

    private BeanUtilsBean beanUtilsBean = new BeanUtilsBean() {
        @Override
        public void copyProperty(Object dest, String name, Object value)
                throws IllegalAccessException, InvocationTargetException {
            if (value == null) return;
            super.copyProperty(dest, name, value);
        }
    };
    private Map<String, Model> models = new LinkedHashMap<>();

    public Map<String, Model> getModels() {
        return models;
    }

    public Model getModel(String databaseId) {
        Model model = models.get(databaseId);
        if (model == null) {
            throw new RuntimeException("database_id " + databaseId + " not found");
        }
        return model;
    }

    public Model create(Model model) {
        model.setDatabaseId(UUID.randomUUID().toString());
        model.setCreatedAt(new Date());
        models.put(model.getDatabaseId(), model);
        return model;
    }

    public Model update(String databaseId, Model model) {
        Model oldModel = this.getModel(databaseId);
        if (oldModel == null) {
            throw new RuntimeException("database_id " + databaseId + " not found");
        }
        try {
            beanUtilsBean.copyProperties(oldModel, model);
            oldModel.setUpdatedAt(new Date());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return oldModel;
    }

    public Model delete(String databaseId) {
        Model deletedModel = models.remove(databaseId);
        if (deletedModel == null) {
            throw new RuntimeException("database_id " + databaseId + " not found");
        }
        return deletedModel;
    }

}
