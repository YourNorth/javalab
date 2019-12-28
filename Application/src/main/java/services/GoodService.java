package services;

import models.Good;
import repositories.GoodRepository;

import java.util.List;

public class GoodService implements Service {

    private GoodRepository goodRepository;

    public List<Good> getGoods() {
        return goodRepository.findAll();
    }
}
