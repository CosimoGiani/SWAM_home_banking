package model;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
public abstract class BaseEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(unique = true)
	private String uuid;
	
	protected BaseEntity() {}

	public BaseEntity(String uuid) {
		if(uuid == null) {
			throw new IllegalArgumentException("uuid cannot be null");
		}
		this.uuid = uuid;
	}

    public Long getId() {
        return id;
    }
    
    public String getUuid() {
    	return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
