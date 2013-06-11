package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "statistic_history")
public class StatisticHistoryEntity implements Serializable {

    //TODO
    private static final long serialVersionUID = -3187757614938904329L;
    private Long id;
    private Date generationDate;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "history_seq")
    @SequenceGenerator(name = "history_seq", sequenceName = "history_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    
    public void setId(long id){
        this.id = id;
    }
    
    @Column(name = "generation_date")
    public Date getGenerationDate(){
        return generationDate;
    }
    
    public void setGenerationDate(Date date){
        generationDate = date;
    }
}
