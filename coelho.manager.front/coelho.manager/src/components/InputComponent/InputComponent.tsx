import TextField from '@mui/material/TextField';

interface InputBoxProps {
  label: string;
  placeholder: string;
  value?: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

export default function InputBox (InputBoxProps: InputBoxProps) {
  return (
    <div>
      <TextField
          required
          id="outlined-required"
          value={InputBoxProps.value}
          onChange={InputBoxProps.onChange}
          label= {InputBoxProps.label}
          placeholder = {InputBoxProps.placeholder}
          slotProps={{
            inputLabel: {
              shrink: true,
            }
          }}
          sx={{
            width: 300,
            bgcolor: 'var(--container-bg)',
            color: 'var(--text-color)',
            mt: 1,
            borderRadius: '6px',
            '& .MuiOutlinedInput-input': {
              color: 'var(--text-color)',
            },
            '& .MuiOutlinedInput-root': {
              backgroundColor: 'var(--container-bg)',
              height: '45px',
              '& fieldset': {
                borderColor: 'var(--border-color)',
              },
              '&:hover fieldset': {
                borderColor: 'var(--primary-color)',
              },
              '&.Mui-focused fieldset': {
                borderColor: 'var(--primary-color)',
              },
            },
            '& .MuiInputLabel-root.Mui-focused': {
                color: 'var(--primary-color)',
            },
            '& .MuiInputLabel-root': {
              color: 'var(--text-color)',
            },
          }}
        />
    </div>
  );
};