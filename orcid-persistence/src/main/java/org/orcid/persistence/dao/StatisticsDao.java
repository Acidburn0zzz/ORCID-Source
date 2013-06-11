package org.orcid.persistence.dao;

public interface StatisticsDao {
    public long createHistory();
    public boolean saveStatistic(long id, String name, double value);
    public double getStatistic(long id, String name);
}
