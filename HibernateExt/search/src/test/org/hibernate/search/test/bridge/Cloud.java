//$Id: $
package org.hibernate.search.test.bridge;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

import org.hibernate.search.annotations.Keyword;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Text;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Indexed
public class Cloud {
    private int id;
    private Long long1;
    private long long2;
    private Integer int1;
    private int int2;
    private Double double1;
    private double double2;
    private Float float1;
    private float float2;
    private String string;
    private Date date;
    private Date dateYear;
    private Date dateMonth;
    private Date dateDay;
    private Date dateHour;
    private Date dateMinute;
    private Date dateSecond;
    private Date dateMillisecond;
    private String customFieldBridge;
    private String customStringBridge;
	private CloudType type;
	private boolean storm;

	@Text
    @FieldBridge(impl = TruncateFieldBridge.class)
    public String getCustomFieldBridge() {
        return customFieldBridge;
    }

    public void setCustomFieldBridge(String customFieldBridge) {
        this.customFieldBridge = customFieldBridge;
    }

    @Text
    @FieldBridge(impl = TruncateStringBridge.class, params = @Parameter( name="dividedBy", value="4" ) )
    public String getCustomStringBridge() {
        return customStringBridge;
    }

    public void setCustomStringBridge(String customStringBridge) {
        this.customStringBridge = customStringBridge;
    }

    @Id @GeneratedValue @Keyword(id=true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Keyword
    public Long getLong1() {
        return long1;
    }

    public void setLong1(Long long1) {
        this.long1 = long1;
    }

    @Keyword
    public long getLong2() {
        return long2;
    }

    public void setLong2(long long2) {
        this.long2 = long2;
    }

    @Keyword
    public Integer getInt1() {
        return int1;
    }

    public void setInt1(Integer int1) {
        this.int1 = int1;
    }

    @Keyword
    public int getInt2() {
        return int2;
    }

    public void setInt2(int int2) {
        this.int2 = int2;
    }

    @Keyword
    public Double getDouble1() {
        return double1;
    }

    public void setDouble1(Double double1) {
        this.double1 = double1;
    }

    @Keyword
    public double getDouble2() {
        return double2;
    }

    public void setDouble2(double double2) {
        this.double2 = double2;
    }

    @Keyword
    public Float getFloat1() {
        return float1;
    }

    public void setFloat1(Float float1) {
        this.float1 = float1;
    }

    @Keyword
    public float getFloat2() {
        return float2;
    }

    public void setFloat2(float float2) {
        this.float2 = float2;
    }

    @Text
	public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Keyword
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Keyword
    @DateBridge( resolution = Resolution.YEAR )
    public Date getDateYear() {
        return dateYear;
    }

    public void setDateYear(Date dateYear) {
        this.dateYear = dateYear;
    }

    @Keyword
    @DateBridge( resolution = Resolution.MONTH )
    public Date getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(Date dateMonth) {
        this.dateMonth = dateMonth;
    }

    @Keyword
    @DateBridge( resolution = Resolution.DAY )
    public Date getDateDay() {
        return dateDay;
    }

    public void setDateDay(Date dateDay) {
        this.dateDay = dateDay;
    }

    @Keyword
    @DateBridge( resolution = Resolution.HOUR )
    public Date getDateHour() {
        return dateHour;
    }

    public void setDateHour(Date dateHour) {
        this.dateHour = dateHour;
    }


    @Keyword
    @DateBridge( resolution = Resolution.MINUTE )
    public Date getDateMinute() {
        return dateMinute;
    }

    public void setDateMinute(Date dateMinute) {
        this.dateMinute = dateMinute;
    }

    @Keyword
    @DateBridge( resolution = Resolution.SECOND )
    public Date getDateSecond() {
        return dateSecond;
    }

    public void setDateSecond(Date dateSecond) {
        this.dateSecond = dateSecond;
    }

    @Keyword
	@DateBridge( resolution = Resolution.MILLISECOND )
    public Date getDateMillisecond() {
        return dateMillisecond;
    }

    public void setDateMillisecond(Date dateMillisecond) {
        this.dateMillisecond = dateMillisecond;
    }

	@Field(index = Index.TOKENIZED )
	public CloudType getType() {
		return type;
	}

	public void setType(CloudType type) {
		this.type = type;
	}

	@Field(index = Index.TOKENIZED )
	public boolean isStorm() {
		return storm;
	}

	public void setStorm(boolean storm) {
		this.storm = storm;
	}
}
