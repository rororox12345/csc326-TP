import React from 'react'
import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { getUserByID, updateUserByID } from '../services/AuthService'

const CustomerComponent = () => {

    const [name, setName] = useState('')
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const { id } = useParams()

    const [errors, setErrors] = useState({})

    const navigate = useNavigate()

    useEffect(() => {
      
            getUserByID(id).then((response) => {
                console.log(response.data)
                setName(response.data.name)
                setEmail(response.data.email)
                setPassword(response.data.password)
                setUsername(response.data.username)
            }).catch(error => {
                console.error(error)
            })
    }, [id])

    function updateCustomer(e) {
        e.preventDefault()
        const user= {name, username, email, password}
        console.log(user)

        setErrors({});

        updateUserByID(id, user).then((response) => {
            console.log(response.data)
            navigate('/manage-customers')
        }).catch(error => {
            console.error(error)
            const errorsCopy = {... errors};
            errorsCopy.general = "Unable to edit Customer\nUsername or email already in use"
            setErrors(errorsCopy)
        })
        
    }

    function pageTitle() {
            return <h2 className='text-center'>Update Customer</h2>
       
    }

  return (
    <div className='container'>
        <br /> <br />
        <div className='row'>
            <div className='card col-md-6 offset-md-3 offset-md-3'>
                <div className='card-header'>
                { pageTitle() }
                </div>
                <div className='card-body'>
                    <form onSubmit={updateCustomer}>
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
                            <label className='col-md-3 control-label' align="right">Username  </label>
                            <div className='col-md-9'>
                                <input
                                    type='text'
                                    name='username'
                                    className='form-control'
                                    required
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                >
                                </input>
                            </div>
                        </div>

                        <div className='row mb-3'>
                            <label className='col-md-3 control-label' align="right">Email  </label>
                            <div className='col-md-9'>
                                <input
                                    type='email'
                                    name='email'
                                    className='form-control'
                                    required
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                >
                                </input>
                            </div>
                        </div>

                        <br></br>

                        <button type='submit' className='btn btn-success'>Submit</button>
                    </form>
                </div>
                    {errors.general && (
                                        <div className="alert alert-danger mt-3" role="alert" style={{ whiteSpace: 'pre-wrap' }}>
                                            {errors.general}
                                        </div>
                                )}
            </div>
        </div>
    </div>
  )
}

export default CustomerComponent