import React, { useEffect, useState } from 'react';
import { getAllCustomers, deleteByID } from '../services/AuthService';
import { useNavigate } from 'react-router-dom'

const ListCustomerComponent = () => {

  const [customer, setCustomer] = useState([]);
  const [errors, setErrors] = useState(null);

  const navigate = useNavigate()
  useEffect(() => {
    const fetchCustomer = async () => {
      try {
        const response = await getAllCustomers();
        setCustomer(response.data); // Adjust if needed
        console.log(response.data)

      } catch (error) {
        setErrors('Failed to load customer data.');
        console.error('Error fetching customer:', error);
      }
    };

    fetchCustomer();
  }, []);

  const getGeneralErrors = () => {
    return errors ? <div className="alert alert-danger">{errors}</div> : null;
  };

  const handleEdit = (customerId) => {
    navigate(`/update-customer/${customerId}`)
    
  };

  const handleDelete = async (customerId) => {
    try {
      await deleteByID(customerId);
      setCustomer((prevCustomer) => prevCustomer.filter((person) => person.id !== customerId));
      console.log("Deleted user with id: " + customerId);
    } catch (error) {
      setErrors('Failed to delete customer ');
      console.error('Error customer customer:', error);
    }
  };

  return (
    <div className="container">
      <br></br>
      <h2 className="text-center">Customers</h2>

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
          {customer.map((person) => (
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
          {customer.length === 0 && (
            <tr>
              <td colSpan="4" className="text-center text-muted">
                No customers found.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    
    </div>
  );
};

export default ListCustomerComponent;
