import React, { useEffect, useState } from 'react';
import { getAllStaff, deleteByID } from '../services/AuthService';
import { useNavigate } from 'react-router-dom'

const ListStaffComponent = () => {

  const [staff, setStaff] = useState([]);
  const [errors, setErrors] = useState(null);

  const navigate = useNavigate()
  useEffect(() => {
    const fetchStaff = async () => {
      try {
        const response = await getAllStaff();
        setStaff(response.data); // Adjust if needed
        console.log(response.data)

      } catch (error) {
        setErrors('Failed to load staff data.');
        console.error('Error fetching staff:', error);
      }
    };

    fetchStaff();
  }, []);

  const getGeneralErrors = () => {
    return errors ? <div className="alert alert-danger">{errors}</div> : null;
  };

  const handleEdit = (id) => {
    console.log(id)
    navigate(`/update-staff/${id}`)
  };
  

  const handleDelete = async (staffId) => {
    try {
      await deleteByID(staffId);
      setStaff((prevStaff) => prevStaff.filter((person) => person.id !== staffId));
      console.log("Deleted user with id: " + staffId);
    } catch (error) {
      setErrors('Failed to delete staff member.');
      console.error('Error deleting staff:', error);
    }
  };
  function addNewStaff() {
    navigate('/create-staff')
  }
  
  return (
    <div className="container">
      <br></br>
      <h2 className="text-center">Staff</h2>

      <br></br>
      {getGeneralErrors()}
      <table className="table table-striped table-bordered">
        <thead>
          <tr>
            <th>Name</th>
            <th>Username</th>
            <th>Email</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {staff.map((person) => (
            <tr key={person.id}>
              <td>{person.name}</td>
              <td>{person.username}</td>
              <td>{person.email}</td>
              {/* <td>
                <button
                  className="btn btn-primary"
                  onClick={() => handleEdit(person.id)}
                >
                  Edit
                </button>
                <button
                  className="btn btn-danger"
                  onClick={() => handleDelete(person.id)}
                  style={{ marginLeft: '10px' }}
                >
                  Delete
                </button>
              </td> */}
              <td>
                  <button className="btn btn-secondary" onClick={() => handleEdit(person.id)}
                      style={{marginLeft: '10px', width: '50px'}}
                  >✏️</button>
                  <button className="btn btn-danger" onClick={() => handleDelete(person.id)}
                      style={{marginLeft: '10px', color: 'white', width: '50px'}}
                  >Ｘ</button>
              </td> 
            </tr>
          ))}
          {staff.length === 0 && (
            <tr>
              <td colSpan="4" className="text-center text-muted">
                No staff found.
              </td>
            </tr>
          )}
        </tbody>
      </table>
      <button className='btn btn-success' onClick={(e) => addNewStaff(e)}>Add Staff</button>
    </div>
  );
};

export default ListStaffComponent;
