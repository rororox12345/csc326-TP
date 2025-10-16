import React from 'react'
import { useEffect, useState } from 'react'
import { getItemById, saveItem, updateItem } from '../services/ItemService'
import { useNavigate, useParams } from 'react-router-dom'

const ItemComponent = () => {

    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
	const [price, setPrice] = useState('')
    const { id } = useParams()

    const [errors, setErrors] = useState({})

    const navigate = useNavigate()

    useEffect(() => {
        if(id) {
            getItemById(id).then((response) => {
                console.log(response.data)
                setName(response.data.name)
                setDescription(response.data.description)
				setPrice(response.data.price)
            }).catch(error => {
                console.error(error)
            })
        }
    }, [id])

    function saveOrUpdateItem(e) {
        e.preventDefault()
        const item = {name, description, price}
        console.log(item)

        if (id) {
            updateItem(id, item).then((response) => {
                setErrors({});
                console.log(response.data)
                navigate('/items')
            }).catch(error => {
                setErrors({general: "Unable to save Item!"});
                console.error(error)
            })
        } else {
            saveItem(item).then((response) => {
                setErrors({});
                console.log(response.data)
                navigate('/items')
            }).catch(error => {
                setErrors({general: "Unable to update Item!"});
                console.error(error)
            })
        }
    }

    function pageTitle() {
        if (id) {
            return <h2 className='text-center'>Update Item</h2>
        } else {
            return <h2 className='text-center'>Add Item</h2>
        }
    }

  return (
    <div className='container'>
        <br /> <br />
        <div className='row'>
            <div className='card col-md-6 offset-md-3 offset-md-3'>
                { pageTitle() }
                
                <div className='card-body'>
                    <form onSubmit={saveOrUpdateItem}>
                        <div className='row mb-3'>
                            <label className='col-md-3 control-label' align="right">Name  </label>
                            <div className='col-md-9'>
                                <input
                                    type='text'
                                    name='name'
                                    className='form-control'
                                    required
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                >
                                </input>
                            </div>
                        </div>

                        <div className='row mb-3'>
                            <label className='col-md-3 control-label' align="right">Description  </label>
                            <div className='col-md-9'>
                                <input
                                    type='text'
                                    name='description'
                                    className='form-control'
                                    required
                                    value={description}
                                    onChange={(e) => setDescription(e.target.value)}
                                >
                                </input>
                            </div>
                        </div>

                        <div className='row mb-3'>
                            <label className='col-md-3 control-label' align="right">Price  </label>
                            <div className='col-md-9'>
                                <input
                                    type='number'
                                    name='prive'
                                    className='form-control'
                                    required
                                    min="1"
                                    step="1"
                                    value={price}
                                    onChange={(e) => setPrice(e.target.value)}
                                >
                                </input>
                            </div>
                        </div>

                        <button type='submit' className='btn btn-success' >Submit</button>
                    </form>

                    {errors.general && (
                                    <div className="alert alert-danger mt-3" role="alert" style={{ whiteSpace: 'pre-wrap' }}>
                                        {errors.general}
                                    </div>
                            )}
                </div>
            </div>
        </div>
    </div>
  )
}

export default ItemComponent;