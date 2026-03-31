import { parseJwt, handleLogError } from './Helpers'

describe('parseJwt', () => {
  it('returns undefined for null input', () => {
    expect(parseJwt(null)).toBeUndefined()
  })

  it('returns undefined for undefined input', () => {
    expect(parseJwt(undefined)).toBeUndefined()
  })

  it('decodes a valid JWT payload', () => {
    // Build a real base64url token the same way test-utils does
    const payload = { sub: 'alice', rol: ['USER'], name: 'Alice', exp: 9999999999 }
    const body = btoa(JSON.stringify(payload))
      .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
    const token = `header.${body}.sig`

    const result = parseJwt(token)
    expect(result.sub).toBe('alice')
    expect(result.rol).toEqual(['USER'])
    expect(result.name).toBe('Alice')
    expect(result.exp).toBe(9999999999)
  })
})

describe('handleLogError', () => {
  beforeEach(() => {
    vi.spyOn(console, 'log').mockImplementation(() => {})
  })

  afterEach(() => {
    console.log.mockRestore()
  })

  it('logs error.response.data when response is present', () => {
    const error = { response: { data: { message: 'Not found' } } }
    handleLogError(error)
    expect(console.log).toHaveBeenCalledWith(error.response.data)
  })

  it('logs error.request when only request is present', () => {
    const error = { request: 'the request object' }
    handleLogError(error)
    expect(console.log).toHaveBeenCalledWith(error.request)
  })

  it('logs error.message for generic errors', () => {
    const error = { message: 'Network Error' }
    handleLogError(error)
    expect(console.log).toHaveBeenCalledWith('Network Error')
  })
})
