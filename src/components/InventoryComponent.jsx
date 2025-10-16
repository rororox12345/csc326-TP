import { useEffect, useState } from "react";
import { getInventory, updateInventory } from "../services/InventoryService";
import { createIngredient } from "../services/IngredientsService";

const InventoryComponent = () => {

    // The state will be an array of objects each of the form:
    // { id: number, name: string, quantity: number }
    const [inventory, setInventory] = useState([]);

    const [newIngredientName, setNewIngredientName] = useState("");
    const [newIngredientQuantity, setNewIngredientQuantity] = useState("");
    
    const [showAddForm, setShowAddForm] = useState(false);

    const [success, setSuccess] = useState({});
    const [errors, setErrors] = useState({});

    useEffect(() => {
        getThisInventory();
    }, []);

    // GETs the inventory from the backend and converts the object into an array
    const getThisInventory = () => {

        getInventory().then((response) => {
            setInventory(response.data);
            console.log(response.data)
        }).catch(error => {
            console.error(error);
        })
    };

    // Updates the quantity for a given inventory item (by id)
    const handleQuantityChange = (id, newValue) => {
        
        setInventory(prevInventory => {
            return prevInventory.map(item => {
                if (item.id === id) {
                return { ...item, quantity: parseInt(newValue, 10) };
                }
                return item;
            });
        });
    };

    // Sends the updated inventory back to the backend.
    const handleUpdateInventory = (e) => {

        e.preventDefault();

        updateInventory( inventory ).then((response) => {
            console.log(response.data);
            setSuccess({inventory: "Updated Successfully!"});
            
            setErrors({});

        }).catch((error) => {
            console.error(error);

            setErrors({inventory: "Failed to update inventory"});

            setSuccess({});
        });
    };

    // Sends new ingredient to the backend. Updates state if successful.
    const handleAddIngredient = (e) => {
        
        e.preventDefault();

        // create ingredient object
        const intQuantity = parseInt(newIngredientQuantity, 10);
        const ingredient = {name: newIngredientName, initialQuantity: intQuantity};
        console.log(ingredient);

        createIngredient(ingredient).then((response) => {
                
            console.log(response.data);

            // reset form
            setShowAddForm(!showAddForm);
            setNewIngredientName("");
            setNewIngredientQuantity("");

            // add ingredient locally
            const newInventoryItem = { id: response.data.id, name: ingredient.name, quantity: ingredient.initialQuantity };
            setInventory([...inventory, newInventoryItem]);   
            
            setErrors({});
            setSuccess({ inventory: `Added Ingredient "${ingredient.name}" successfully!` });
          
        }).catch(error => {

            console.error(error);
            setSuccess({});
            if (error.response.status == 409) {
                setErrors({ name: "Duplicate Ingredient name!"});
            }
            return;
        })
    };

return (
  <div className="container">
    <br /><br />
    
    <div className="row">
      <div className="card col-md-6 offset-md-3">
        
        <h2 className="text-center">Inventory</h2>
        <div className="card-body">
          
          <br />
          <div className="d-flex flex-column align-items-center mb-3">
            <button
              className="btn btn-primary mb-2"
              onClick={() => setShowAddForm(!showAddForm)}
            >
              {showAddForm ? "Cancel" : "Add Ingredient"}
            </button>
          </div>


          {/* Add New Ingredient Form */}
          {showAddForm && (
            <form className="mb-5" onSubmit={handleAddIngredient}>
              <div className="row">
                <div className="col-md-5">
                  <input
                    type="text"
                    placeholder="Ingredient Name"
                    required
                    name="ingredient name"
                    value={newIngredientName}
                    onChange={(e) => setNewIngredientName(e.target.value)}
                    className={`form-control ${errors.name ? "is-invalid":""}`}
                  />
                  {errors.name && <div className="invalid-feedback">{errors.name}</div>}
                </div>
                <div className="col-md-5">
                  <input
                    type="number"
                    placeholder="Initial Quantity"
                    required
                    min="1"
                    step="1"
                    name="ingredient initial quantity"
                    value={newIngredientQuantity}
                    className="form-control"
                    onChange={(e) => setNewIngredientQuantity(e.target.value)}
                  />
                </div>
                <div className="col-md-2">
                  <button type="submit" className="btn btn-success">
                      Add
                  </button>
                </div>
              </div>
            </form>
          )}


          <form onSubmit={handleUpdateInventory}>
            {/* Display the inventory in a table */}
            <table className="table table-bordered">
              <thead>
                <tr>
                  <th>Ingredient</th>
                  <th>Quantity</th>
                </tr>
              </thead>
              <tbody>
                {inventory.map((item) => (
                  <tr key={item.id}>
                    <td>{item.name}</td>
                    <td>
                      <input
                        type="number"
                        value={Number.isFinite(item.quantity) ? item.quantity : ""}
                        min="1"
                        step="1"
                        required
                        name="ingredient quantity"
                        className="form-control"
                        onChange={(e) => handleQuantityChange(item.id, e.target.value)}
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            <br />
            <button type="submit" className="btn btn-primary mb-2">Update Inventory</button>

            {success.inventory && (
              <div className="alert alert-success mt-3" role="alert">
                {success.inventory}
              </div>
            )}

          </form>
        </div>
      </div>
    </div>
    <br />
  </div>
)};

export default InventoryComponent;
