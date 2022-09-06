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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BaseEntity)) {
			return false;
		}
		BaseEntity other = (BaseEntity) obj;
		return uuid.equals(other.getUuid());
	}

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
