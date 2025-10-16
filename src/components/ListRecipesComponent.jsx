import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { listRecipes, deleteRecipe } from '../services/RecipesService';
import { getInventory } from '../services/InventoryService';

/** Lists all the recipes and provide the option to create a new recipe
 * and delete an existing recipe.
 */
const ListRecipesComponent = () => {

    const [recipes, setRecipes] = useState([]);
    const [inventory, setInventory] = useState([]);
    const [successMessage, setSuccessMessage] = useState("");

    const navigator = useNavigate();

    useEffect(() => {
        getAllRecipes();
        getAllInventory();
    }, []);

    const getAllRecipes = () => {
        listRecipes()
            .then((response) => {
                const formattedRecipes = response.data.map(recipe => {
                    return {
                        ...recipe,
                        ingredients: recipe.recipeIngredientsResponseList.map(i => i.name),
                        ingredientAmounts: recipe.recipeIngredientsResponseList.map(i => i.quantity)
                    };
                });
                setRecipes(formattedRecipes);
            })
            .catch(error => {
                console.error(error);
            });
    };
    

    const getAllInventory = () => {
        getInventory().then((response) => {
            setInventory(response.data);
        }).catch(error => {
            console.error(error);
        });
    };

    const addNewRecipe = () => {
        navigator('/add-recipe');
    };

    const editRecipe = (id) => {
        navigator(`/edit-recipe/${id}`);
    };

    const removeRecipe = (recipe) => {
        console.log(recipe.id);
        deleteRecipe(recipe.id).then((response) => {
            getAllRecipes();
            setSuccessMessage(`Deleted recipe "${recipe.name}" successfully!`);
            setTimeout(() => {
                setSuccessMessage("");
            }, 3000);
        }).catch(error => {
            console.error(error);
        });
    };

    return (
        <div className="container">
            <br />
            <h2 className="text-center">Recipes</h2>
            <br />
            <button className="btn btn-primary mb-2" onClick={addNewRecipe}>Add Recipe</button>
            <br />

            {inventory.length > 0 && recipes.length > 0 && (
                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <th>Recipe Name</th>
                            <th>Recipe Price</th>
                            {inventory.map(ingredient => (
                                <th key={ingredient.name}>Amount {ingredient.name}</th>
                            ))}
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {recipes.map(recipe => (
                            <tr key={recipe.name}>
                                <td>{recipe.name}</td>
                                <td>{recipe.price}</td>
                                {inventory.map(ingredient => {
                                    const cellKey = `${recipe.name}-${ingredient.name}`;

                                    // ✅ Defensive checks to avoid undefined errors
                                    if (
                                        Array.isArray(recipe.ingredients) &&
                                        Array.isArray(recipe.ingredientAmounts)
                                    ) {
                                        const idx = recipe.ingredients.indexOf(ingredient.name);
                                        if (idx !== -1) {
                                            return <td key={cellKey}>{recipe.ingredientAmounts[idx]}</td>;
                                        }
                                    }

                                    return <td key={cellKey} style={{ backgroundColor: '#c9c9c9' }}>—</td>;
                                })}
                                <td>
                                    <button
                                        className="btn btn-secondary"
                                        onClick={() => editRecipe(recipe.id)}
                                        style={{ marginLeft: '10px', width: '50px' }}
                                    >✏️</button>
                                    <button
                                        className="btn btn-danger"
                                        onClick={() => removeRecipe(recipe)}
                                        style={{ marginLeft: '10px', color: 'white', width: '50px' }}
                                    >Ｘ</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}

            {successMessage && (
                <div className="alert alert-warning mt-3" role="alert">
                    {successMessage}
                </div>
            )}
        </div>
    );
};

export default ListRecipesComponent;
