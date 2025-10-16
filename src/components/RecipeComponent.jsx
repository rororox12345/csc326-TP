import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { createRecipe, getRecipe, updateRecipe } from '../services/RecipesService';
import { getInventory } from '../services/InventoryService';

const RecipeComponent = () => {

    const { id } = useParams()

    const [name, setName] = useState("");
    const [price, setPrice] = useState("");
    // list of ingredients for recipe as objects with {id, quantity}
    const [ingredients, setIngredients] = useState([]);

    // list of all ingredients available as objects with {id, name, quantity (in inventory)}
    const [allIngredients, setAllIngredients] = useState([]);

    const [isFrozen, setIsFrozen] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [errors, setErrors] = useState({});

    const navigator = useNavigate();

    useEffect(() => {

        // Edit recipe case
        if (id) {
            getRecipe(id).then((response) => {
                console.log("Found recipe ", response.data);
                setName(response.data.name);
                setPrice(response.data.price);
                setIngredients(response.data.recipeIngredientsResponseList);
            }).catch(error => {
                console.error(error);
            })
        }

        getInventory().then((response) => {
            setAllIngredients(response.data);
            console.log("Ingredients ", response.data);
        }).catch((error) => {
            console.error(error);
        });

    }, [id]);

    const handleCheckBox = (isChecked, ingredient) => {
        if (isChecked) {
            setIngredients((prev) => [...prev, {id: ingredient.id, quantity: ""}]);
        } else {
            setIngredients((prev) => prev.filter(item => item.id !== ingredient.id));
        }
    };

    const handleAmount = (amount, ingredient) => {
        // update amount
        setIngredients(ingredients.map(i => 
            i.id === ingredient.id ? 
            { ...i, quantity: amount } : i
        ));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const intPrice = parseInt(price, 10);

        const recipeIngredientsRequestList = ingredients.map((ingredient, i) => ({
            id: ingredient.id,
            quantity: parseInt(ingredient.quantity, 10)
        }));

        const recipe = {
            name,
            price: intPrice,
            recipeIngredientsRequestList
        };

        console.log(recipe);

        if (allIngredients.length === 0) {
            setErrors({ general: "No ingredients in inventory! Redirecting to Inventory..." });
            setIsFrozen(true);
            setTimeout(() => navigator('/inventory'), 2500);
            return;
        }

        if (ingredients.length === 0) {
            setErrors({ general: "Select at least one ingredient!" });
            return;
        }
        
        // Edit recipe case
        if ( id ) {

            updateRecipe(id, recipe).then((response) => {
                console.log(response.data);
                setIsFrozen(true);
                setErrors({});
                setSuccessMessage(`Edited Recipe successfully!`)
                setTimeout(() => navigator('/recipes'), 2500);
            }).catch(error => {
                console.error(error);
            })

        // Create recipe case
        } else {

            createRecipe(recipe).then((response) => {
                console.log(response.data);

                setIsFrozen(true);
                setSuccessMessage(`Added Recipe "${name}" successfully!`);
                setErrors({});
                setTimeout(() => navigator('/recipes'), 2500);

            }).catch((error) => {

                console.error(error);
                if (error.response?.status === 409) {
                    setErrors({name: "Duplicate Recipe name!"});
                } else if (error.response?.status === 507) {
                    setErrors({general: "Cannot add more than 3 Recipes!"});
                }
            });
          }
    };

    return (
      <div className="container">
          <br /><br />

          <div className="row">
            <div className="card col-md-6 offset-md-3">

              <h2 className="text-center">{id? "Edit Recipe" : "Add Recipe" }</h2>

              <div className="card-body">
                <form onSubmit={handleSubmit}>

                  <div className="row mb-3">
                    <label className="col-md-3 control-label" align="right">Name  </label>
                    <div className="col-md-7">
                      <input
                        type="text"
                        name="name"
                        value={name}
                        required
                        onChange={(e) => setName(e.target.value)}
                        className={`form-control ${errors.name ? "is-invalid" : ""}`}
                        disabled={isFrozen}
                      />
                      {errors.name && <div className="invalid-feedback">{errors.name}</div>}
                    </div>
                  </div>

                  <div className="row mb-3">
                    <label className="col-md-3 control-label" align="right">Price  </label>
                    <div className="col-md-7">
                      <input
                        type="number"
                        name="price"
                        value={price}
                        min="1"
                        step="1"
                        required
                        onChange={(e) => setPrice(e.target.value)}
                        className="form-control"
                        disabled={isFrozen}
                      />
                    </div>
                  </div>
                  
                  <br />

                  {allIngredients.length > 0 && (
                    <table className="table table-bordered">
                      <thead>
                        <tr>
                          <th>Select</th>
                          <th>Ingredient Name</th>
                          <th>Quantity</th>
                        </tr>
                      </thead>
                      
                      <tbody>
                        {allIngredients.map((ingredient) => (
                          <tr key={ingredient.id}>
                            <td>
                              <div className="form-check d-flex justify-content-center align-items-center">
                                <input
                                  type="checkbox"
                                  className="form-check-input position-static"
                                  name="ingredient checkbox"
                                  defaultChecked={ingredients?ingredients.some(i => i.id === ingredient.id):false}
                                  onChange={(e) => handleCheckBox(e.target.checked, ingredient)}
                                  disabled={isFrozen}
                                />
                              </div>
                            </td>

                            <td>{ingredient.name}</td>
                            
                            <td>
                              <div className="form-group mb-2">
                                <input
                                  type="number"
                                  min="1"
                                  step="1"
                                  required
                                  name="ingredient quantity"
                                  value={ingredients.find(i => i.id === ingredient.id)?.quantity || ""}
                                  className="form-control form-control-sm"
                                  onChange={(e) => handleAmount(e.target.value, ingredient)}
                                  disabled={isFrozen || !ingredients.some(i => i.id === ingredient.id)}
                                />                                
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}

                  <br /><br />
                  
                  <button type="submit" className="btn btn-success">Save</button>
                </form>
              </div>

              {successMessage && (
                <div className="alert alert-success mt-3">{successMessage}</div>
              )}
              
              {errors.general && (
                <div className="alert alert-danger mt-3">{errors.general}</div>
              )}

            </div>
          </div>
      </div>
    );
};

export default RecipeComponent;
